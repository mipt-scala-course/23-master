package hw.traverse

import cats.Applicative

import java.time.ZonedDateTime

object Template:
  opaque type S = String

  def apply(s: String): S = s

type Template = Template.S

object Rendered:
  opaque type S = String

  def apply(s: String): S = s

type Rendered = Rendered.S

trait Localization[F[_]]:
  def localize: Template => F[Rendered]

case class Form[A](
    title: A,
    tags: List[A],
    subForms: List[Form[A]],
    date: ZonedDateTime
)

object FormLocalization:

  def localize[F[_]: Applicative: Localization](f: Form[Template]): F[Form[Rendered]] = ???