package com.unknown.sancheck.util

import com.unknown.sancheck.data.local.entity.Book
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import java.io.OutputStream

class ExcelManager {

    private val headers = listOf(
        "ISBN13", "제목", "부제", "저자", "옮긴이", "출판사", "출간일",
        "쪽수", "가격", "별점", "서가ID", "수량", "메모", "등록일",
        "밀리보유", "윌라보유"
    )

    fun exportToExcel(books: List<Book>, outputStream: OutputStream) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("도서목록")

        // Header row
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, header ->
            headerRow.createCell(index).setCellValue(header)
        }

        // Data rows
        books.forEachIndexed { rowIndex, book ->
            val row = sheet.createRow(rowIndex + 1)
            row.createCell(0).setCellValue(book.isbn13)
            row.createCell(1).setCellValue(book.title)
            row.createCell(2).setCellValue(book.subtitle)
            row.createCell(3).setCellValue(book.author)
            row.createCell(4).setCellValue(book.translator)
            row.createCell(5).setCellValue(book.publisher)
            row.createCell(6).setCellValue(book.pubDate)
            row.createCell(7).setCellValue(book.pageCount.toDouble())
            row.createCell(8).setCellValue(book.priceStandard.toDouble())
            row.createCell(9).setCellValue(book.rating.toDouble())
            row.createCell(10).setCellValue(book.bookshelfId.toDouble())
            row.createCell(11).setCellValue(book.quantity.toDouble())
            row.createCell(12).setCellValue(book.memo)
            row.createCell(13).setCellValue(book.dateAdded.toDouble())
            row.createCell(14).setCellValue(if (book.availableOnMillie) "Y" else "N")
            row.createCell(15).setCellValue(if (book.availableOnWelaaa) "Y" else "N")
        }

        workbook.write(outputStream)
        workbook.close()
    }

    fun importFromExcel(inputStream: InputStream): List<Book> {
        val books = mutableListOf<Book>()
        val workbook = XSSFWorkbook(inputStream)
        val sheet = workbook.getSheetAt(0)

        for (rowIndex in 1..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex) ?: continue
            try {
                val book = Book(
                    isbn13 = row.getCell(0)?.stringCellValue ?: "",
                    title = row.getCell(1)?.stringCellValue ?: "",
                    subtitle = row.getCell(2)?.stringCellValue ?: "",
                    author = row.getCell(3)?.stringCellValue ?: "",
                    translator = row.getCell(4)?.stringCellValue ?: "",
                    publisher = row.getCell(5)?.stringCellValue ?: "",
                    pubDate = row.getCell(6)?.stringCellValue ?: "",
                    pageCount = row.getCell(7)?.numericCellValue?.toInt() ?: 0,
                    priceStandard = row.getCell(8)?.numericCellValue?.toInt() ?: 0,
                    rating = row.getCell(9)?.numericCellValue?.toFloat() ?: 0f,
                    bookshelfId = row.getCell(10)?.numericCellValue?.toLong() ?: 1L,
                    quantity = row.getCell(11)?.numericCellValue?.toInt() ?: 1,
                    memo = row.getCell(12)?.stringCellValue ?: "",
                    dateAdded = row.getCell(13)?.numericCellValue?.toLong()
                        ?: System.currentTimeMillis(),
                    availableOnMillie = row.getCell(14)?.stringCellValue == "Y",
                    availableOnWelaaa = row.getCell(15)?.stringCellValue == "Y"
                )
                books.add(book)
            } catch (_: Exception) {
                // Skip malformed rows
            }
        }

        workbook.close()
        return books
    }
}
