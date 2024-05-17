package hw

class Tests extends munit.FunSuite:
  test("I. 2 reduction"):
    val xx = x =>: x(x)
    assertEquals(
      reduce(id(xx)),
      xx
    )

    assertEquals(
      reduce(`if`(`true`)(x)(y)),
      x
    )

    assertEquals(
      reduce(`if`(`false`)(x)(y)),
      y
    )

  test("I. 3.а) not"):
    assertEquals(
      reduce(`not`(`true`)),
      `false`
    )

    assertEquals(
      reduce(`not`(`true`)),
      `false`
    )

  test("I. 3.б) times"):
    assertEquals(
      reduce(times(`1`)(`2`)),
      `2`
    )

    assertEquals(
      reduce(times(`2`)(`3`)),
      `6`
    )

    assertEquals(
      reduce(times(`0`)(`8`)),
      `0`
    )

  test("I. 3.в) power"):
    assertEquals(
      reduce(power(`0`)(`2`)),
      `0`
    )

    assertEquals(
      reduce(power(`1`)(`2`)),
      `1`
    )

    assertEquals(
      reduce(power(`2`)(`3`)),
      `8`
    )

    assertEquals(
      reduce(power(`2`)(`0`)),
      `1`
    )

    assertEquals(
      reduce(power(`0`)(`0`)),
      `1`
    )

  test("I. 3.г) iszero"):
    assertEquals(
      reduce(iszero(`0`)),
      `true`
    )

    assertEquals(
      reduce(iszero(`1`)),
      `false`
    )

    assertEquals(
      reduce(iszero(`2`)),
      `false`
    )
