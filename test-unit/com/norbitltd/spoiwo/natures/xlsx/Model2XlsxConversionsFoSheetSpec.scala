package com.norbitltd.spoiwo.natures.xlsx

import org.scalatest.FlatSpec
import org.apache.poi.xssf.usermodel.{XSSFCellStyle, XSSFSheet, XSSFWorkbook}
import Model2XlsxConversions.convertSheet
import com.norbitltd.spoiwo.model.{Column, Font, CellStyle, Sheet}
import com.norbitltd.spoiwo.model.Height._
import scala.util.Try

class Model2XlsxConversionsFoSheetSpec extends FlatSpec {

  private def workbook : XSSFWorkbook = new XSSFWorkbook()

  private def convert(s : Sheet) : XSSFSheet = convertSheet(s, workbook)

  private def defaultSheet = convert(Sheet())

  "Sheet conversions" should "set sheet name to 'Sheet1' by default in empty workbook" in {
    assert(defaultSheet.getSheetName == "Sheet1")
  }

  it should "set sheet name to 'Sheet 3'in a workbook with already 2 sheets defined" in {
    val w : XSSFWorkbook = workbook
    w.createSheet("Test1")
    w.createSheet("Test2")
    val sheet3 : XSSFSheet = convertSheet(Sheet(), w)
    assert(sheet3.getSheetName == "Sheet3")
  }

  it should "return defined name when set explicitly" in {
    val model = Sheet(name = "My Name")
    val xssf = convert(model)
    assert(xssf.getSheetName === "My Name")
  }

  it should "not have 1st column style by default" in {
    val xssfFont = defaultSheet.getColumnStyle(0).asInstanceOf[XSSFCellStyle].getFont
    assert(xssfFont.getFontName == "Calibri")
    assert(xssfFont.getFontHeightInPoints == 11)
  }

  it should "have 1st column style when set with column without index" in {
    val style = CellStyle(font = Font(fontName = "Arial", height = 14 points))
    val model = Sheet(columns = Column(style = style) :: Nil)

    val xssf = convert(model)
    val xssfFont = xssf.getColumnStyle(0).asInstanceOf[XSSFCellStyle].getFont
    assert(xssfFont.getFontName == "Arial")
    assert(xssfFont.getFontHeightInPoints == 14)
  }

  it should "have correct 1st and 2nd column style when set with column without index" in {
    val column1 = Column(style = CellStyle(font = Font(fontName = "Arial", height = 14 points)))
    val column2 = Column(style = CellStyle(font = Font(fontName = "Arial", height = 16 points)))
    val model = Sheet(columns = column1 :: column2 :: Nil)
    val xssf = convert(model)

    val xssfFont1 = xssf.getColumnStyle(0).asInstanceOf[XSSFCellStyle].getFont
    assert(xssfFont1.getFontName == "Arial")
    assert(xssfFont1.getFontHeightInPoints == 14)

    val xssfFont2 = xssf.getColumnStyle(1).asInstanceOf[XSSFCellStyle].getFont
    assert(xssfFont2.getFontName == "Arial")
    assert(xssfFont2.getFontHeightInPoints == 16)
  }

  it should "have 3rd column style when set with column with index" in {
    val style = CellStyle(font = Font(fontName = "Arial", height = 14 points))
    val model = Sheet(columns = Column(style = style, index = 2) :: Nil)

    val xssf = convert(model)
    val xssfFont = xssf.getColumnStyle(2).asInstanceOf[XSSFCellStyle].getFont
    assert(xssfFont.getFontName == "Arial")
    assert(xssfFont.getFontHeightInPoints == 14)
  }

  it should "have correct 4th and 6th column style when set with column without index" in {
    val column1 = Column(style = CellStyle(font = Font(fontName = "Arial", height = 14 points)), index = 3)
    val column2 = Column(style = CellStyle(font = Font(fontName = "Arial", height = 16 points)), index = 5)
    val model = Sheet(columns = column1 :: column2 :: Nil)
    val xssf = convert(model)

    val xssfFont1 = xssf.getColumnStyle(3).asInstanceOf[XSSFCellStyle].getFont
    assert(xssfFont1.getFontName == "Arial")
    assert(xssfFont1.getFontHeightInPoints == 14)

    val xssfFont2 = xssf.getColumnStyle(4).asInstanceOf[XSSFCellStyle].getFont
    assert(xssfFont2.getFontName == "Calibri")
    assert(xssfFont2.getFontHeightInPoints == 11)

    val xssfFont3 = xssf.getColumnStyle(5).asInstanceOf[XSSFCellStyle].getFont
    assert(xssfFont3.getFontName == "Arial")
    assert(xssfFont3.getFontHeightInPoints == 16)
  }

  it should "not allow mixing columns with and without index" in {
    val column1 = Column(style = CellStyle(font = Font(fontName = "Arial", height = 14 points)))
    val column2 = Column(style = CellStyle(font = Font(fontName = "Arial", height = 16 points)), index = 5)
    val model = Sheet(columns = column1 :: column2 :: Nil)
    val xlsx = Try( convert(model) )
    assert(xlsx.isFailure)
  }

  it should "not allow 2 columns with the same index" in {
    val column1 = Column(style = CellStyle(font = Font(fontName = "Arial", height = 14 points)), index = 3)
    val column2 = Column(style = CellStyle(font = Font(fontName = "Arial", height = 16 points)), index = 5)
    val column3 = Column(style = CellStyle(font = Font(fontName = "Arial", height = 16 points)), index = 3)
    val model = Sheet(columns = column1 :: column2 :: column3 :: Nil)
    val xlsx = Try( convert(model) )
    assert(xlsx.isFailure)
  }

}
