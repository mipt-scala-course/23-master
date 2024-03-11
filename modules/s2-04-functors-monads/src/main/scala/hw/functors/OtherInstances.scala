package hw.functors

import cats.Functor

object OtherInstances:
  type Arr[-C, +A] = C => (List[A] => C) => A

  /** Реализуйте инстанс Functor для Arr[C, *] (по второму аргументу)
    */
  given [C]: Functor[Arr[C, *]] = ???
