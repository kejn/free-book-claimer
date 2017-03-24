package com.github.kejn.packtpub;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;

/**
 * Defines WebDrivers supported by app.
 *
 * @author kejn
 */
enum SupportedDriver {
    FIREFOX("webdriver.gecko.driver", FirefoxDriver.class),
    CHROME("webdriver.chrome.driver", ChromeDriver.class),
    OPERA("webdriver.opera.driver", OperaDriver.class),
    INTERNET_EXPLORER("webdriver.ie.driver", InternetExplorerDriver.class),
    EDGE("webdriver.edge.driver", EdgeDriver.class);

    private final String propertyKey;
    private final Class<? extends WebDriver> driverClass;

    SupportedDriver(String propertyKey, Class<? extends WebDriver> driverClass) {
        this.propertyKey = propertyKey;
        this.driverClass = driverClass;
    }

    /**
     * Gets the first found driver defined in environment variables.
     *
     * @return new instance of found WebDriver
     * @throws IllegalAccessException if WebDriver's no-arg constructor is not accessible
     * @throws InstantiationException if the WebDriver's instantiation fails for some reason
     */
    static WebDriver getAvailableDriver() throws IllegalAccessException, InstantiationException {
        for (SupportedDriver aDriver : values()) {
            String driverPath = System.getProperty(aDriver.propertyKey);
            if (driverPath != null) {
                return aDriver.driverClass.newInstance();
            }
        }
        throw new IllegalStateException("Path to WebDriver is not defined in a proper environment variable.");
    }

    @Override
    public String toString() {
        return String.format("  %-22s - %s", driverClass.getSimpleName(), propertyKey);
    }
}
