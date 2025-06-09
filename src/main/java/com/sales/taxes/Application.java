package com.sales.taxes;

import com.sales.taxes.model.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final String INPUT_FILE_PATH = "src/main/resources/";
    private static final String OUTPUT_FILE_PATH = "src/main/resources/";
    private static final String INPUT_FILE_NAME = "input";
    private static final String OUTPUT_FILE_NAME = "output";
    private static final String FILE_EXTENSION = ".txt";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        runTaxesCalculator(
                INPUT_FILE_PATH + INPUT_FILE_NAME + FILE_EXTENSION,
                OUTPUT_FILE_PATH + OUTPUT_FILE_NAME + FILE_EXTENSION
        );
    }

    private static void runTaxesCalculator (String inputFilePath, String outputFilePath) {
        log.info("Start runTaxesCalculator");
        try {
            writeOutputFile(
                    outputFilePath,
                    parseArticleInFile(
                            extractedLinesInInputFile(inputFilePath)
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            log.info("End runTaxesCalculator");
        }

    }

    private static void writeOutputFile (String outputFilePath, List<Article> articleList) throws IOException{
        File outputFile = new File(outputFilePath);
        if (outputFile.createNewFile()) {
            log.info("File created: " + outputFile.getName());
            writeArticlesAndTotalInOutputFile(articleList, outputFilePath);
        } else {
            log.info("File already exists. Remove file and retry !");
        }
    }

    private static void writeArticlesAndTotalInOutputFile (List<Article> articleList, String outputFilePath) throws IOException {
        FileWriter outputFileWriter = new FileWriter(outputFilePath);
        List<String> articleListString = articleList.stream().map(Article::toString).toList();
        for (String a : articleListString) {
            outputFileWriter.write(a + '\n');
        }
        outputFileWriter.write(getTotalSalesTaxesAndPrice(articleList));
        outputFileWriter.close();
    }

    private static List<Article> parseArticleInFile (List<String> lines) {
        List<Article> articleList = new ArrayList<>();
        lines.forEach(line -> {
            articleList.add(extractArticle(line));
        });
        return articleList;
    }

    private static String getTotalSalesTaxesAndPrice (List<Article> articleList) {
        return "Sales Taxes: " + String.format("%.2f", getSalesTaxes(articleList)) + " Total: " + String.format("%.2f", getTotalPrice(articleList));

    }

    private static double getSalesTaxes (List<Article> articleList) {
        return articleList.stream().mapToDouble(article -> (article.getPriceWithTaxes() - article.getPrice())*article.getQuantity()).sum();
    }

    private static double getTotalPrice (List<Article> articleList) {
        return articleList.stream().mapToDouble(article -> {
            return article.getPriceWithTaxes()*article.getQuantity();
        }).sum();
    }

    private static Article extractArticle(String line) {
        Article article;
        int quantity = parseQuantity(line);
        String name = parseName(line);
        double price = parsePrice(line);
        article = new Article(quantity, name, price, basicTaxesisApplicable(line), isImportedArticle(line));
        return article;
    }

    private static boolean isImportedArticle(String line) {
        return line.contains("imported");
    }

    private static boolean basicTaxesisApplicable(String line) {
        if(line.contains("book")) {
            return false;
        } else if (line.contains("chocolate")) {
            return false;
        } else if (line.contains("pills")) {
            return false;
        }
        return true;
    }

    private static int parseQuantity(String line) {
        int result = 0;
        String quantityRegex = "\\d+";
        Pattern pattern = Pattern.compile(quantityRegex);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            result = Integer.parseInt(matcher.group(0));
        }
        return result;
    }

    private static String parseName(String line) {
        return line.substring(2, line.lastIndexOf("at"));
    }

    private static double parsePrice(String line) {
        return Double.parseDouble(line.substring(line.lastIndexOf("at")+3));
    }

    private static List<String> extractedLinesInInputFile (String inputFilePath) throws FileNotFoundException{
        return getReader(inputFilePath).lines().toList();
    }

    private static BufferedReader getReader (String inputFilePath) throws FileNotFoundException {
        File inputFile = new File(inputFilePath);
        FileReader inputFileReader = null;
        inputFileReader = new FileReader(inputFile);
        return new BufferedReader(inputFileReader);
    }
}
