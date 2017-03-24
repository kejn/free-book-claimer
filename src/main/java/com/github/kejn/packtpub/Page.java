package com.github.kejn.packtpub;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Defines a Page at https://www.packtpub.com site.
 *
 * @author kejn
 */
public class Page {
    private static final Logger LOGGER = LoggerFactory.getLogger(Page.class);

    private static final String INITIAL_URL = "https://www.packtpub.com";
    private static final int WAIT_BETWEEN_REQUESTS = 5000;

    private WebDriver driver;
    private JavascriptExecutor jsExecutor;

    /**
     * Creates instance using given <b>driver</b> and navigates to home page.
     *
     * @param driver the driver which will be used to navigate on the Page
     * @throws IllegalArgumentException if the <b>driver</b> is not able to execute JavaScript code
     */
    public Page(WebDriver driver) {
        if (!(driver instanceof JavascriptExecutor)) {
            throw new IllegalArgumentException("The " + driver.getClass().getSimpleName() + " does not support JavaScript execution");
        }
        this.driver = driver;
        this.jsExecutor = (JavascriptExecutor) driver;

        driver.navigate().to(INITIAL_URL);

        LOGGER.debug("Page initialized");
    }

    /**
     * Logs user in using provided credentials.
     *
     * @param username the username/email
     * @param password the password
     * @return this Page instance
     * @throws IOException if I/O error occurs during file reading
     */
    public Page login(String username, String password) throws IOException {
        String jQuery = readJQueryFrom("/scripts/login.js")
                .replace("{{username}}", username)
                .replace("{{password}}", password);
        executeJs(jQuery, new By.ById("account-bar-logged-in"));

        LOGGER.info("Logged in as '" + username + "'");

        return this;
    }


    /**
     * Navigates to page where user can claim their free book.
     * @return this Page instance
     */
    public Page navigateToFreeBook() {
        WebElement div = driver.findElement(new By.ByClassName("hero-blocks"));//> div.hero-block-body > h3
        List<WebElement> links = div.findElements(new By.ByTagName("a"));
        for (WebElement a : links) {
            String text = StringUtils.defaultString(a.getAttribute("href"));
            if (StringUtils.containsIgnoreCase(text, "free-learning")) {
                driver.navigate().to(text);
                waitForAWhile();
                return this;
            }
        }
        throw new IllegalStateException("Page does not contain the link to free ebooks");
    }

    /**
     * Claims currently available free book.
     * @return this Page instance
     */
    public Page claimYourFreeBook() {
        String jQuery = wrapJQuery("$('input.form-submit').click();");
        executeJs(jQuery, new By.ById("account-right-content"));

        LOGGER.info("Book claimed");

        return this;
    }


    public Page logout() {
        String jQuery = wrapJQuery("$('#account-bar-logged-in a:nth-child(3) div:nth-child(1)').click()");
        getWait().until(ExpectedConditions.visibilityOfElementLocated(new By.ByCssSelector("#account-bar-logged-in a:nth-child(3) div:nth-child(1)")));
        executeJs(jQuery, null);

        LOGGER.info("Logged out");

        return this;
    }

    private String readJQueryFrom(String path) throws IOException {
        URL url = getClass().getResource(path);
        return FileUtils.readFileToString(new File(url.getPath()), Charset.defaultCharset());
    }

    private void executeJs(String jQuery, By waitUntilVisibleElementLocatedBy) {
        jsExecutor.executeScript(jQuery);
        if (waitUntilVisibleElementLocatedBy != null) {
            getWait().until(ExpectedConditions.visibilityOfElementLocated(waitUntilVisibleElementLocatedBy));
        }
        waitForAWhile();
    }

    private WebDriverWait getWait() {
        return new WebDriverWait(driver, 60);
    }

    private void waitForAWhile() {
        try {
            Thread.sleep(WAIT_BETWEEN_REQUESTS);
        } catch (InterruptedException e) {
            LOGGER.error("Could not sleep", e);
        }
    }

    private String wrapJQuery(String code) {
        return "$(document).ready(function() {" + code + "});";
    }

}
