package com.github.kejn.packtpub.pages;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
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
     */
    public Page login(String username, String password) throws IOException, URISyntaxException {
        String jQuery = readJQueryFrom("scripts/login.js")
                .replace("{{username}}", username)
                .replace("{{password}}", password);
        executeJs(jQuery, new By.ById("account-bar-logged-in"));

        LOGGER.info("Logged in as '" + username + "'");

        return this;
    }


    /**
     * Navigates to page where user can claim their free book.
     *
     * @return this Page instance
     */
    public Page navigateToFreeBook() {
        WebElement div = driver.findElement(new By.ByClassName("hero-blocks"));
        List<WebElement> links = div.findElements(new By.ByTagName("a"));
        for (WebElement a : links) {
            String text = StringUtils.defaultString(a.getAttribute("href"));
            if (StringUtils.containsIgnoreCase(text, "free-learning")) {
                LOGGER.info("Navigating to free book");
                driver.navigate().to(text);
                return this;
            }
        }
        throw new IllegalStateException("Page does not contain the link to free ebooks");
    }

    /**
     * Claims currently available free book.
     *
     * @return this Page instance
     * @throws IOException if I/O error occurs during script file reading
     */
    public Page claimYourFreeBook() throws IOException, URISyntaxException {
        String jQuery = readJQueryFrom("scripts/claimFreeBook.js");
        executeJs(jQuery, new By.ById("account-right-content"));

        LOGGER.info("Book claimed");

        return this;
    }


    @SuppressWarnings("UnusedReturnValue")
    public Page logout() {
        String selector = "#account-bar-logged-in a:nth-child(3) div:nth-child(1)";
        String jQuery = wrapJQuery("$('" + selector + "').click()");
        executeJs(jQuery);

        LOGGER.info("Logged out");

        return this;
    }

    private String readJQueryFrom(String path) throws IOException {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(path), Charset.defaultCharset());
    }

    private void executeJs(String jQuery) {
        executeJs(jQuery, null);
    }

    private void executeJs(String jQuery, By waitUntilVisibleElementLocatedBy) {
        jsExecutor.executeScript(jQuery);
        if (waitUntilVisibleElementLocatedBy != null) {
            getWait().until(ExpectedConditions.visibilityOfElementLocated(waitUntilVisibleElementLocatedBy));
        }
    }

    private WebDriverWait getWait() {
        return new WebDriverWait(driver, 60);
    }

    private String wrapJQuery(String code) {
        return "$(document).ready(function() {" + code + "});";
    }

}
