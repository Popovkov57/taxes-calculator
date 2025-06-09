package com.sales.taxes;

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
public class TaxesCalculatorApplication {
	private static final Logger log = LoggerFactory.getLogger(TaxesCalculatorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(TaxesCalculatorApplication.class, args);
		String inputFilePath = "src/main/resources/input.txt";
		List<String> lines = extractedLines(inputFilePath);
		List<Article> articleList = getArticles(lines);

		try {
			File outputFile = new File("src/main/resources/output.txt");
			if (outputFile.createNewFile()) {
				log.info("File created: " + outputFile.getName());
				writeArticlesAndTotalInOutputFile(articleList);
			} else {
				log.info("File already exists. Remove file and retry !");
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}

	}

	private static void writeArticlesAndTotalInOutputFile (List<Article> articleList) throws IOException {
		FileWriter outputFileWriter = new FileWriter("src/main/resources/output.txt");
		List<String> articleListString = articleList.stream().map(Article::toString).toList();
		for (String a : articleListString) {
			outputFileWriter.write(a + '\n');
		}
		outputFileWriter.write(getTotalSalesTaxesAndPrice(articleList));
		outputFileWriter.close();
	}

	private static List<Article> getArticles (List<String> lines) {
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
		int quantity = parseQuantity(line);
		String name = parseName(line);
		double price = parsePrice(line);
		return new Article(quantity, name, price, basicTaxesisApplicable(line), isImportedArticle(line));
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

	private static List<String> extractedLines (String inputFilePath) {
		return getReader(inputFilePath).lines().toList();
	}

	private static BufferedReader getReader (String inputFilePath) {
		File inputFile = new File(inputFilePath);
        FileReader inputFileReader = null;
        try {
            inputFileReader = new FileReader(inputFile);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        }
		return inputFileReader != null ? new BufferedReader(inputFileReader) : null;
	}
}
