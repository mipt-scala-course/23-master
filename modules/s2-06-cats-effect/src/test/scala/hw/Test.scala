package hw

import cats.effect.{Ref, IO, Temporal, Outcome}
import cats.effect.std.{UUIDGen, Random, Console}
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.Show
import org.scalatest.matchers.should.Matchers
import org.scalatest.freespec.AsyncFreeSpec
import cats.syntax.all.*
import cats.effect.syntax.all.*
import scala.concurrent.duration.{Duration, DurationInt}

enum HistoryEvent:
  case Start, Finish

type HistoryLog[In] = (In, WorkerId, HistoryEvent)

// wraps pure function to worker and simulates activity
def mkWorker[
  F[_]: Ref.Make: Console: UUIDGen: Random: Temporal,
  In: Show,
  Out: Show
](
   f: In => Out,
   sleep: F[Duration],
   history: Ref[F, List[HistoryLog[In]]]
 ): F[Worker[F, In, Out]] =
  for
    id <- UUIDGen[F].randomUUID
    workerId = WorkerId(id)
    counter <- Ref.of[F, Int](0) // number of times worker is executed
  yield
    Worker(
      workerId,
      (in: In) => {
        val res = f(in)
        history.update((in, workerId, HistoryEvent.Start) :: _) >>
          Console[F].println(show"Starting by $id, input: $in") >>
          sleep.flatMap(Temporal[F].sleep) >>
          Console[F].println(show"Finishing by $id, output: $res") >>
          history.update((in, workerId, HistoryEvent.Finish) :: _) >>
          counter.modify(x => (x + 1, x + 1)).flatMap(x =>
            Console[F].println(s"Total processed by $id: $x")
          ).as(res)
      }
    )

def mkWorkers[
  F[_]: Ref.Make: Console: UUIDGen: Temporal,
  In: Show,
  Out: Show
](
   f: In => Out,
   sleep: F[Duration],
   count: Int,
   random: F[Random[F]],
   history: Ref[F, List[HistoryLog[In]]]
 ): F[List[Worker[F, In, Out]]] =
  for
    given Random[F] <- random
    res <- List.fill(count)(f).traverse(mkWorker(_, sleep, history))
  yield res

class TaskSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers:
  "Worker Pool" - {
    def tasks3(parallelism: Int) =
      for {
        historyR <- Ref.of[IO, List[HistoryLog[Int]]](Nil)
        workers <- mkWorkers[IO, Int, Int](
          _ + 1,
          100.millis.pure,
          parallelism,
          Random.scalaUtilRandom[IO],
          historyR)
        pool <- WorkerPool.of(workers)

        fstF  <- pool.run(0).start
        _     <- IO.sleep(10.millis)
        sndF  <- pool.run(1).start
        _     <- IO.sleep(10.millis)
        thrdF <- pool.run(2).start
        fst   <- fstF.join
        snd   <- sndF.join
        thrd  <- thrdF.join

        res <- (fst, snd, thrd) match
          case (Outcome.Succeeded(f), Outcome.Succeeded(s), Outcome.Succeeded(t)) =>
            (f, s, t).mapN((x, y, z) => (x, y, z))
          case _ => IO.raiseError(new Exception("smth failed"))
        history <- historyR.get.map(_.map((in, _, ev) => (in, ev)).reverse)
      } yield (res, history)

    "executes tasks sequentially for one worker" in {
      tasks3(1).asserting(_ shouldBe (
        (1, 2, 3),
        ((0, HistoryEvent.Start) ::
          (0, HistoryEvent.Finish) ::
          (1, HistoryEvent.Start) ::
          (1, HistoryEvent.Finish) ::
          (2, HistoryEvent.Start) ::
          (2, HistoryEvent.Finish) :: Nil)
      )
      )
    }

    "executes tasks in parallel for multiple workers" in {
      tasks3(3).asserting(_ shouldBe (
        (1, 2, 3),
        ((0, HistoryEvent.Start) ::
          (1, HistoryEvent.Start) ::
          (2, HistoryEvent.Start) ::
          (0, HistoryEvent.Finish) ::
          (1, HistoryEvent.Finish) ::
          (2, HistoryEvent.Finish) :: Nil)
      )
      )
    }

    "awaits for free worker" in {
      tasks3(2).asserting(_ shouldBe (
        (1, 2, 3),
        ((0, HistoryEvent.Start) ::
          (1, HistoryEvent.Start) ::
          (0, HistoryEvent.Finish) ::
          (2, HistoryEvent.Start) ::
          (1, HistoryEvent.Finish) ::
          (2, HistoryEvent.Finish) :: Nil)
      )
      )
    }

    "plays well when cancelling fibers" in {
      val res = for {
        historyR <- Ref.of[IO, List[HistoryLog[Int]]](Nil)
        workers <- mkWorkers[IO, Int, Int](
          _ + 1,
          100.millis.pure,
          2,
          Random.scalaUtilRandom[IO],
          historyR)
        pool <- WorkerPool.of(workers)

        fstF  <- pool.run(0).start
        _     <- IO.sleep(10.millis)
        sndF  <- pool.run(1).start
        _     <- IO.sleep(10.millis)
        _     <- fstF.cancel
        thrdF <- pool.run(2).start
        snd   <- sndF.join
        thrd  <- thrdF.join

        res <- (snd, thrd) match
          case (Outcome.Succeeded(s), Outcome.Succeeded(t)) =>
            (s, t).mapN((x, y) => (x, y))
          case _ => IO.raiseError(new Exception("smth failed"))
        history <- historyR.get.map(_.map((in, _, ev) => (in, ev)).reverse)
      } yield (res, history)

      res.asserting(_ shouldBe (
        (2, 3),
        ((0, HistoryEvent.Start) ::
          (1, HistoryEvent.Start) ::
          (2, HistoryEvent.Start) ::
          (1, HistoryEvent.Finish) ::
          (2, HistoryEvent.Finish) :: Nil)
      )
      )
    }

    "should not terminate when removing workers" in {
      val res = for
        historyR <- Ref.of[IO, List[HistoryLog[Int]]](Nil)
        workers <- mkWorkers[IO, Int, Int](
          _ + 1,
          100.millis.pure,
          1,
          Random.scalaUtilRandom[IO],
          historyR)
        pool <- WorkerPool.of(workers)
        removed =
          for
            _    <- pool.run(0).start
            _    <- IO.sleep(10.millis)
            _    <- pool.removeAll
            _    <- pool.run(1)
          yield ()
        fib <- removed.start
        _ <- IO.sleep(100.millis)
        _ <- fib.cancel
        out <- fib.join
        history <- historyR.get.map(_.map((in, _, ev) => (in, ev)).reverse)
      yield (out.isCanceled, history)

      res.asserting(_ shouldBe (
        true,
        (0, HistoryEvent.Start) :: (0, HistoryEvent.Finish) :: Nil)
      )
    }

    "can add workers" in {
      val res = for
        historyR <- Ref.of[IO, List[HistoryLog[Int]]](Nil)
        workers <- mkWorkers[IO, Int, Int](
          _ + 1,
          100.millis.pure,
          1,
          Random.scalaUtilRandom[IO],
          historyR)
        pool <- WorkerPool.of(workers)

        _    <- pool.run(0).start
        _    <- IO.sleep(10.millis)
        _    <- pool.removeAll
        fib  <- pool.run(1).start
        _    <- IO.sleep(100.millis)
        _    <- workers.traverse(pool.add)
        outF <- fib.join
        out  <- outF match
          case Outcome.Succeeded(o) => o
          case _ => IO.raiseError(new Exception("smth failed"))
        history <- historyR.get.map(_.map((in, _, ev) => (in, ev)).reverse)
      yield (out, history)

      res.asserting(_ shouldBe (
        2,
        (0, HistoryEvent.Start) ::
          (0, HistoryEvent.Finish) ::
          (1, HistoryEvent.Start) ::
          (1, HistoryEvent.Finish) :: Nil)
      )
    }
  }
