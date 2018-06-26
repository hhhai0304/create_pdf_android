package vn.dmspro.createpdf2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

/**
 * Class for PDF helper
 * Created by Hai Ho on 25/07/2017.
 */

public class PdfHelper {
    private final String TAG = getClass().getSimpleName();
    private Context context;
    private Document document;

    private String rightHeader;
    private String pageTitle;

    private static Font boldFont;

    public PdfHelper(Context context) {
        this.context = context;
        boldFont = new Font();
        boldFont.setStyle(Font.BOLD);

        rightHeader = "OEC Computers Australia\n/ LEVEL 1 - 168 WALKER STREET";
        pageTitle = "SALES INVOICE";
    }

    public void createPdfFile(String filePath, String fileName) {
        if (!fileName.endsWith(".pdf")) {
            fileName += ".pdf";
        }
        String filePathWithName = filePath + "/" + fileName;

        document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePathWithName));
            document.open();
            for (int i = 0; i < 10; i++) {
                document.add(getDmsProHeader(rightHeader));
                document.add(getTitle(pageTitle));
                if (i != 9) {
                    document.newPage();
                }
            }
            document.add(getOrderHeader());
            document.close();
        } catch (Exception e) {
            Log.e(TAG, "createPdfFile() " + e.getMessage());
        }
    }

    /**
     * Lấy logo DMSpro để chèn vô PDF
     *
     * @return Image
     */
    private Image getDmsLogo() {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_for_pdf);
        double ratio = bitmap.getWidth() / bitmap.getHeight();
        double newWidth = 130;
        double newHeight = newWidth / ratio;

        Bitmap resizedImage = Bitmap.createScaledBitmap(bitmap, (int) newWidth, (int) newHeight, false);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        try {
            return Image.getInstance(stream.toByteArray());
        } catch (Exception e) {
            Log.e(TAG, "getDmsLogo() " + e.getMessage());
        }
        return null;
    }

    /**
     * Header của từng trang PDF, bao gồm logo DMSpro (bên trái) và tên bên nhận (bên phải)
     *
     * @param rightText chữ bên phải (có thể \n để xuống dòng)
     * @return PdfPTable
     */
    private PdfPTable getDmsProHeader(String rightText) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        Font font = new Font();
        font.setStyle(Font.BOLD);
        font.setSize(14);

        PdfPCell leftCell = new PdfPCell(getDmsLogo());
        leftCell.setBorder(Rectangle.NO_BORDER);
        PdfPCell rightCell = new PdfPCell(new Phrase(rightText, font));
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(leftCell);
        table.addCell(rightCell);
        return table;
    }

    /**
     * Tiêu đề của trang PDF
     *
     * @param title tiêu đề (in đậm, ở giữa trang, dưới header)
     * @return Paragraph
     */
    private Paragraph getTitle(String title) {
        Font font = new Font();
        font.setStyle(Font.BOLD);
        font.setSize(22);
        Paragraph paragraph = new Paragraph(title, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        return paragraph;
    }

    /**
     * Header cho trang gồm thông tin của Khách hàng và Salesman
     * @return PdfPTable
     */
    private PdfPTable getOrderHeader() {
        PdfPTable table = new PdfPTable(new float[]{(float) 1.5, (float) 3.5, 1, 3, 1});
        table.setWidthPercentage(100f);

        Font boldFont = new Font();
        boldFont.setStyle(Font.BOLD);

        // Margin top trước khi add OrderHeader
        table.setSpacingBefore(12);

        // Dòng 1
        table.addCell(getOrderHeaderCell("Customer:", true));
        table.addCell(getOrderHeaderCell("C20000 - Maxi Teq:", true));
        table.addCell(getOrderHeaderCell("Invoice No.:", true));
        table.addCell(getOrderHeaderCell("366", true));
        table.addCell(getOrderHeaderCell("2/11/2017", false));

        // Dòng 2
        table.addCell(getOrderHeaderCell("", true));
        table.addCell(getOrderHeaderCell("", true));
        table.addCell(getOrderHeaderCell("Salesman:", true));
        table.addCell(getOrderHeaderCell("Salesman 2 -", true));
        table.addCell(getOrderHeaderCell("", true));

        // Dòng 3
        table.addCell(getOrderHeaderCell("Federal Tax ID:", true));
        table.addCell(getOrderHeaderCell("79 933 596 217", true));
        table.addCell(getOrderHeaderCell("Phone:", true));
        table.addCell(getOrderHeaderCell("0868603494", true));
        table.addCell(getOrderHeaderCell("", true));

        return table;
    }

    /**
     * Từng ô trong phần Order Header
     * @param value text của từng ô
     * @param isAlignLeft true: canh lề trái - false: canh lề phải
     * @return PdfPCell
     */
    private PdfPCell getOrderHeaderCell(String value, boolean isAlignLeft) {
        PdfPCell cell = new PdfPCell(new Phrase(value, boldFont));
        cell.setBorder(Rectangle.NO_BORDER);
        if (!isAlignLeft) {
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        }
        return cell;
    }

    private PdfPTable getTable() {
        // Create a table with 7 columns
        PdfPTable table = new PdfPTable(new float[]{2, 1, 2, 5, 1, 3, 2});
        table.setWidthPercentage(100f);
        table.getDefaultCell().setUseAscender(true);
        table.getDefaultCell().setUseDescender(true);
        // Add the first header row
        Font f = new Font(Font.FontFamily.TIMES_ROMAN);
        f.setColor(BaseColor.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase("Day", f));
        cell.setBackgroundColor(BaseColor.BLACK);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setColspan(7);
        table.addCell(cell);
        // Add the second header row twice
        table.getDefaultCell().setBackgroundColor(BaseColor.LIGHT_GRAY);
        for (int i = 0; i < 2; i++) {
            table.addCell("Location");
            table.addCell("Time");
            table.addCell("Run Length");
            table.addCell("Title");
            table.addCell("Year");
            table.addCell("Directors");
            table.addCell("Countries");
        }
        table.getDefaultCell().setBackgroundColor(null);
        // There are three special rows
        table.setHeaderRows(3);
        // One of them is a footer
        table.setFooterRows(1);
        // Now let's loop over the screenings
        for (int i = 0; i < 10; i++) {
            table.addCell("1:1");
            table.addCell("2h00");
            table.addCell("600");
            table.addCell("Phim hoạt hình");
            table.addCell("2017");
            cell = new PdfPCell(new Phrase("Day", f));
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Day", f));
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            table.addCell(cell);
        }
        return table;
    }

    private PdfPTable createFirstTable() {
        // a table with three columns
        PdfPTable table = new PdfPTable(3);
        // the cell object
        PdfPCell cell;
        // we add a cell with colspan 3
        cell = new PdfPCell(new Phrase("Cell with colspan 3"));
        cell.setColspan(3);
        table.addCell(cell);
        // now we add a cell with rowspan 2
        cell = new PdfPCell(new Phrase("Cell with rowspan 2"));
        cell.setRowspan(2);
        table.addCell(cell);
        // we add the four remaining cells with addCell()
        table.addCell("row 1; cell 1");
        table.addCell("row 1; cell 2");
        table.addCell("row 2; cell 1");
        table.addCell("row 2; cell 2");
        return table;
    }
}