import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
     public static void main(String[]args) throws IOException {
         System.out.println("Enter the player's name:");
         String playerName;
         try (BufferedReader buffRead = new BufferedReader(new InputStreamReader(System.in))) {
             playerName = buffRead.readLine();
         }
         String url = "https://www.nba.com/search?filters=player&q=" + playerName + "&sortBy=rel";
         System.setProperty("webdriver.gecko.driver","c:/GeckoDriver/geckodriver.exe");
         WebDriver driver = new FirefoxDriver();
         driver.get(url);
         try {
             Thread.sleep(5000);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
         driver.findElement(By.id("onetrust-accept-btn-handler")).click();
         try {
             Thread.sleep(5000);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
         Document docPlayer = Jsoup.parse(driver.getPageSource());
         Element link = docPlayer.select("[data-id='nba:search-results:result:card']").first();
         if (link == null) {
             System.out.println("Player not found");
             driver.quit();
             return;
         }
         String urlPlayerId = link.attr("href");
         urlPlayerId = urlPlayerId.substring(0, urlPlayerId.lastIndexOf("/")+1);
         driver.navigate().to("https://www.nba.com/stats" + urlPlayerId + "?Season=2020-21&SeasonType=Regular Season&PerMode=Per40");
         try {
             Thread.sleep(5000);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
         Document docStats = Jsoup.parse(driver.getPageSource());
         Element table;
         try {
             table = docStats.select("nba-stat-table[template='player/player-traditional']").first().select("tbody").first();
         } catch (Exception e) {
             System.out.println("Stats not available for the selected player");
             driver.quit();
             return;
         }
         Elements rows;
         try {
             rows = table.select("tr");
         } catch (Exception e) {
             System.out.println("Empty table");
             driver.quit();
             return;
         }
         rows.forEach((row) -> {
             Elements td = row.select("td");
             Element season = td.get(0);
             Element average = td.get(9);
             String seasonS = season.text();
             String averageS = average.text();
             System.out.println(seasonS + " " + averageS);
         });
         driver.quit();
    }
}
