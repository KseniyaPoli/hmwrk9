package tests;
import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.Configuration;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import domain.Candidate;
import helpers.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class FileParseTest {
    ClassLoader classLoader = FileParseTest.class.getClassLoader();
    Path archive = Paths.get("src/test/resources/archive.zip");

    @BeforeAll
    static void browserParams() {
        Configuration.timeout = 10000; //10 sec
        Configuration.browserSize = "1920x1080";
        Configuration.remote = "https://user1:1234@selenoid.autotests.cloud/wd/hub";
    }

    @Test
    void readCsvFromZip() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(archive));
             CSVReader csvReader = new CSVReader(new InputStreamReader(zis, UTF_8))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (FileUtils.fileExtension(entry.getName()).equals("csv")) {
                    List<String[]> result = csvReader.readAll();
                    assertThat(result).contains(
                            new String[]{"date", "food", "cafes", "entertainment", "summaryTL", "summaryRb"},
                            new String[]{"16.10", "500", "1", "12", "512", "1792"},
                            new String[]{"17.10", "127.5", "120", "2", "247", "866"});
                }
            }
        }
    }


    @Test
    void readPdfFromZip() throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(archive))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (FileUtils.fileExtension(entry.getName()).equals("pdf")) {
                    PDF pdf = new PDF(zis.readAllBytes());
                    assertThat(pdf.text).contains(
                            "Школа инженеров по автоматизации тестирования на Java"
                    );
                }
            }
        }
    }

    @Test
    void readExcelFromZip() throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(archive))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (FileUtils.fileExtension(entry.getName()).equals("xls")) {
                    XLS xls = new XLS(zis.readAllBytes());
                    assertThat(xls.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue()).contains("category");
                    assertThat(xls.excel.getSheetAt(0).getRow(1).getCell(1).getNumericCellValue()).isEqualTo(12);
                    assertThat(xls.excel.getSheetAt(0).getRow(2).getCell(0).getStringCellValue()).contains("cucumber");
                    assertThat(xls.excel.getSheetAt(0).getRow(4).getCell(0).getStringCellValue()).contains("meat");
                    assertThat(xls.excel.getSheetAt(0).getRow(4).getCell(1).getNumericCellValue()).isEqualTo(50);
                }
            }
        }
    }

    @Test
    void readJson() throws IOException {
        try (InputStream is = classLoader.getResourceAsStream("testJson.json")) {
            ObjectMapper mapper = new ObjectMapper();
            Candidate candidate = mapper.readValue(Objects.requireNonNull(is).readAllBytes(), Candidate.class);
            assertThat(candidate.getLanguages()).contains(
                    "Korean",
                    "English"
            );
            assertThat(candidate.isReadyForWork()).isEqualTo(true
            );
        }
    }
}
