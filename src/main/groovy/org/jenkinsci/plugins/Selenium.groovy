package org.jenkinsci.plugins

import org.jenkinsci.complex.axes.ItemList

class Selenium {

    def seleniumCapabilities = new ItemList<? extends SeleniumCapability>()
    def seleniumLatest = new ItemList<? extends SeleniumCapability>()
    def seleniumSelected = new ItemList<? extends SeleniumCapability>()

    def seleniumVer
    def browsers = []
    def platforms = []
    def versions = []

    Selenium( ISeleniumCapabilityReader reader, Class<? extends SeleniumCapability> clazz  ) {

        def latestMap = [:]

        reader.capabilities.each {
            SeleniumCapability n = clazz.newInstance(it.api_name, it.os, it.short_version ?: 'Any', 'SEL')
            if (seleniumCapabilities.contains(n)) {
                seleniumCapabilities.get(seleniumCapabilities.indexOf(n)).incr()
                if ( it.api_name == 'internet explorer' ) {
                    latestMap["${it.api_name}-${it.os}-${it.short_version}"] = n
                } else if ( it.short_version != 'beta'
                        && latestMap["${it.api_name}-${it.os}"].browserVersion < it.short_version ) {
                    latestMap["${it.api_name}-${it.os}"] = n

                }
            } else {
                seleniumCapabilities.add(n)
                if ( it.api_name == 'internet explorer' ) {
                    latestMap["${it.api_name}-${it.os}-${it.short_version}"] = n
                } else if ( it.short_version != 'beta' ) {
                    latestMap["${it.api_name}-${it.os}"] = n
                }
            }
        }
        seleniumLatest = new ItemList<? extends SeleniumCapability>(latestMap.values())

        seleniumLatest.each {
            if (['internet explorer', 'chrome', 'safari', 'firefox'].contains(it.browserName)) {
                seleniumSelected << it
            }
        }

        Collections.sort(seleniumCapabilities)

        seleniumCapabilities.each { aSel ->
            //seleniumCapabilityDescriptor.add(new SeleniumCapability.SeleniumCapabilityDescriptor(aSel))

            if (aSel.browserName && !browsers.contains(aSel.browserName)) {
                browsers << aSel.browserName
            }
            if (aSel.platformName && !platforms.contains(aSel.platformName)) {
                platforms << aSel.platformName
            }
            if (aSel.browserVersion && !versions.contains(aSel.browserVersion)) {
                versions << aSel.browserVersion
            }
        }
        Collections.sort(browsers)
        Collections.sort(platforms)
        Collections.sort(versions)
    }

    List<? extends SeleniumCapability> getSeleniumCapability() {
        seleniumCapabilities
    }

    //SeleniumCapability random() {
    //    seleniumCapabilities.get(randomizer.nextInt(seleniumCapabilities.size()))
    //}
}
