package net.rrm.ehour.ui
package chart

import net.rrm.ehour.report.reports.AggregateReportDataObjectMother
import net.rrm.ehour.ui.common.BaseSpringWebAppTester
import net.rrm.ehour.ui.report.aggregate.{ChartContext, AggregateReportChartGenerator}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite, Matchers}

@RunWith(classOf[JUnitRunner])
class UserChartGeneratorTest extends FunSuite with Matchers with BeforeAndAfter {
  var chart: String = _

  before {
    BaseSpringWebAppTester.startEmptyPanel()

    val reportData = AggregateReportDataObjectMother.generateReportData

    chart = AggregateReportChartGenerator.generateUserReportChart(ChartContext("container", reportData, "$", withTurnover = true))
  }

  test("should have minimum height of 400px") {
    chart should include("""chart:{"renderTo":"container","defaultSeriesType":"bar","height":400}""")
  }

  test("should have x axis with users") {
    chart should include("""xAxis:[{"categories":["EdelingRosalie","EdelingThies"]}]""")
  }

  test("should have two y axises with hours and formatted turnover") {
    chart should include("""yAxis:[{"title":{"text":"report.chart.label.hours"}},{"title":{"text":"""")

    chart should include("""},"labels":{"formatter":function() { return this.value.toLocaleString();}},"opposite":true}]""")
  }

  test("should have series with booked hours") {
    chart should include("""series:[{"name":"report.chart.label.hours","data":[22.0,42.0],"yAxis":0}""")
  }

  test("should have series with turnover") {
    chart should include("""{"name":"report.chart.label.turnover","data":[340.0,1110.0],"yAxis":1}]""")
  }
}