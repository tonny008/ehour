package net.rrm.ehour.util

import net.rrm.ehour.AbstractSpec
import org.joda.time.{Interval, LocalDate}

class JodaDateUtilTest extends AbstractSpec {
  "Joda Date Util" should {
    "return enumerate the dates between two dates" in {
      val start = new LocalDate(2014, 1, 1)
      val end = new LocalDate(2014, 1, 5)

      val xs = JodaDateUtil.enumerate(start, end)

      xs should have size 5
      xs(0) should equal(start)
      xs(1) should equal(start.plusDays(1))
      xs(2) should equal(start.plusDays(2))
      xs(3) should equal(start.plusDays(3))
      xs(4) should equal(end)
    }

    "should return empty collection when initial start is after end" in {
      val start = new LocalDate(2014, 1, 6)
      val end = new LocalDate(2014, 1, 5)

      val xs = JodaDateUtil.enumerate(start, end)

      xs should be ('empty)
    }

    "find 2 weekend days in an interval" in {
      val start = new LocalDate(2014, 8, 1)
      val end = new LocalDate(2014, 8, 7)

      val days = JodaDateUtil.findWeekendDays(new Interval(start.toDateTimeAtStartOfDay, end.toDateTimeAtStartOfDay))

      days should have size 2
    }

    "find no weekend days in an interval" in {
      val start = new LocalDate(2014, 8, 5)
      val end = new LocalDate(2014, 8, 7)

      val days = JodaDateUtil.findWeekendDays(new Interval(start.toDateTimeAtStartOfDay, end.toDateTimeAtStartOfDay))

      days should be ('empty)
    }
  }
}
