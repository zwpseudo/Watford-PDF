package watford.software.SPDF.controller.api.filters;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import watford.software.SPDF.model.api.PDFComparisonAndCount;
import watford.software.SPDF.model.api.PDFWithPageNums;
import watford.software.SPDF.model.api.filter.ContainsTextRequest;
import watford.software.SPDF.model.api.filter.FileSizeRequest;
import watford.software.SPDF.model.api.filter.PageRotationRequest;
import watford.software.SPDF.model.api.filter.PageSizeRequest;
import watford.software.SPDF.utils.PdfUtils;
import watford.software.SPDF.utils.WebResponseUtils;

@RestController
@RequestMapping("/api/v1/filter")
@Tag(name = "Filter", description = "Filter APIs")
public class FilterController {

    @PostMapping(consumes = "multipart/form-data", value = "/filter-contains-text")
    @Operation(
            summary = "Checks if a PDF contains set text, returns true if does",
            description = "Input:PDF Output:Boolean Type:SISO")
    public ResponseEntity<byte[]> containsText(@ModelAttribute ContainsTextRequest request)
            throws IOException, InterruptedException {
        MultipartFile inputFile = request.getFileInput();
        String text = request.getText();
        String pageNumber = request.getPageNumbers();

        PDDocument pdfDocument = PDDocument.load(inputFile.getInputStream());
        if (PdfUtils.hasText(pdfDocument, pageNumber, text))
            return WebResponseUtils.pdfDocToWebResponse(
                    pdfDocument, inputFile.getOriginalFilename());
        return null;
    }

    // TODO
    @PostMapping(consumes = "multipart/form-data", value = "/filter-contains-image")
    @Operation(
            summary = "Checks if a PDF contains an image",
            description = "Input:PDF Output:Boolean Type:SISO")
    public ResponseEntity<byte[]> containsImage(@ModelAttribute PDFWithPageNums request)
            throws IOException, InterruptedException {
        MultipartFile inputFile = request.getFileInput();
        String pageNumber = request.getPageNumbers();

        PDDocument pdfDocument = PDDocument.load(inputFile.getInputStream());
        if (PdfUtils.hasImages(pdfDocument, pageNumber))
            return WebResponseUtils.pdfDocToWebResponse(
                    pdfDocument, inputFile.getOriginalFilename());
        return null;
    }

    @PostMapping(consumes = "multipart/form-data", value = "/filter-page-count")
    @Operation(
            summary = "Checks if a PDF is greater, less or equal to a setPageCount",
            description = "Input:PDF Output:Boolean Type:SISO")
    public ResponseEntity<byte[]> pageCount(@ModelAttribute PDFComparisonAndCount request)
            throws IOException, InterruptedException {
        MultipartFile inputFile = request.getFileInput();
        String pageCount = request.getPageCount();
        String comparator = request.getComparator();
        // Load the PDF
        PDDocument document = PDDocument.load(inputFile.getInputStream());
        int actualPageCount = document.getNumberOfPages();

        boolean valid = false;
        // Perform the comparison
        switch (comparator) {
            case "Greater":
                valid = actualPageCount > Integer.parseInt(pageCount);
                break;
            case "Equal":
                valid = actualPageCount == Integer.parseInt(pageCount);
                break;
            case "Less":
                valid = actualPageCount < Integer.parseInt(pageCount);
                break;
            default:
                throw new IllegalArgumentException("Invalid comparator: " + comparator);
        }

        if (valid) return WebResponseUtils.multiPartFileToWebResponse(inputFile);
        return null;
    }

    @PostMapping(consumes = "multipart/form-data", value = "/filter-page-size")
    @Operation(
            summary = "Checks if a PDF is of a certain size",
            description = "Input:PDF Output:Boolean Type:SISO")
    public ResponseEntity<byte[]> pageSize(@ModelAttribute PageSizeRequest request)
            throws IOException, InterruptedException {
        MultipartFile inputFile = request.getFileInput();
        String standardPageSize = request.getStandardPageSize();
        String comparator = request.getComparator();

        // Load the PDF
        PDDocument document = PDDocument.load(inputFile.getInputStream());

        PDPage firstPage = document.getPage(0);
        PDRectangle actualPageSize = firstPage.getMediaBox();

        // Calculate the area of the actual page size
        float actualArea = actualPageSize.getWidth() * actualPageSize.getHeight();

        // Get the standard size and calculate its area
        PDRectangle standardSize = PdfUtils.textToPageSize(standardPageSize);
        float standardArea = standardSize.getWidth() * standardSize.getHeight();

        boolean valid = false;
        // Perform the comparison
        switch (comparator) {
            case "Greater":
                valid = actualArea > standardArea;
                break;
            case "Equal":
                valid = actualArea == standardArea;
                break;
            case "Less":
                valid = actualArea < standardArea;
                break;
            default:
                throw new IllegalArgumentException("Invalid comparator: " + comparator);
        }

        if (valid) return WebResponseUtils.multiPartFileToWebResponse(inputFile);
        return null;
    }

    @PostMapping(consumes = "multipart/form-data", value = "/filter-file-size")
    @Operation(
            summary = "Checks if a PDF is a set file size",
            description = "Input:PDF Output:Boolean Type:SISO")
    public ResponseEntity<byte[]> fileSize(@ModelAttribute FileSizeRequest request)
            throws IOException, InterruptedException {
        MultipartFile inputFile = request.getFileInput();
        String fileSize = request.getFileSize();
        String comparator = request.getComparator();

        // Get the file size
        long actualFileSize = inputFile.getSize();

        boolean valid = false;
        // Perform the comparison
        switch (comparator) {
            case "Greater":
                valid = actualFileSize > Long.parseLong(fileSize);
                break;
            case "Equal":
                valid = actualFileSize == Long.parseLong(fileSize);
                break;
            case "Less":
                valid = actualFileSize < Long.parseLong(fileSize);
                break;
            default:
                throw new IllegalArgumentException("Invalid comparator: " + comparator);
        }

        if (valid) return WebResponseUtils.multiPartFileToWebResponse(inputFile);
        return null;
    }

    @PostMapping(consumes = "multipart/form-data", value = "/filter-page-rotation")
    @Operation(
            summary = "Checks if a PDF is of a certain rotation",
            description = "Input:PDF Output:Boolean Type:SISO")
    public ResponseEntity<byte[]> pageRotation(@ModelAttribute PageRotationRequest request)
            throws IOException, InterruptedException {
        MultipartFile inputFile = request.getFileInput();
        int rotation = request.getRotation();
        String comparator = request.getComparator();

        // Load the PDF
        PDDocument document = PDDocument.load(inputFile.getInputStream());

        // Get the rotation of the first page
        PDPage firstPage = document.getPage(0);
        int actualRotation = firstPage.getRotation();
        boolean valid = false;
        // Perform the comparison
        switch (comparator) {
            case "Greater":
                valid = actualRotation > rotation;
                break;
            case "Equal":
                valid = actualRotation == rotation;
                break;
            case "Less":
                valid = actualRotation < rotation;
                break;
            default:
                throw new IllegalArgumentException("Invalid comparator: " + comparator);
        }

        if (valid) return WebResponseUtils.multiPartFileToWebResponse(inputFile);
        return null;
    }
}
