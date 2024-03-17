package hw.applicatives

import hw.applicatives.DoubleCalculator.*

class Tests extends munit.FunSuite:
  test("Applicatives.1 parse positive int"):
    assert(calculator.parse("1349") == Right("", 1349))

  test("Applicatives.2 parse positive double with fraction part"):
    assert(calculator.parse("13.49") == Right("", 13.49))

  test("Applicatives.3 parse negative double"):
    assert(calculator.parse("-13.49") == Right("", -13.49))

  test("Applicatives.4 parse sum"):
    assert(calculator.parse("12.0+10.5") == Right("", 22.5))

  test("Applicatives.5 parse subtraction"):
    assert(calculator.parse("12.0-10.5") == Right("", 1.5))

  test("Applicatives.6 parse complex expression"):
    assert(calculator.parse("-7-3.0*4/6+10.5*2*2-2") == Right("", 31.0))

  test("Applicatives.7 parse only valid substring"):
    assert(calculator.parse("3*/24sdf") == Right("*/24sdf", 3))
