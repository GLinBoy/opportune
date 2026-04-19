package com.glinboy.opportune.service

/**
 * Provides offline IP geolocation based on the MaxMind GeoLite2-City database.
 */
interface GeoLocationService {

	/**
	 * Look up the country and city for the given IP address.
	 *
	 * @param ip IPv4 or IPv6 address string
	 * @return [GeoLocationResult] if the lookup succeeds, or `null` if the IP is
	 *   unknown, the database is unavailable, or geolocation is disabled.
	 */
	fun lookup(ip: String): GeoLocationResult?

	/**
	 * Formats a [GeoLocationResult] as a human-readable string with a flag emoji,
	 * e.g. `"🇺🇸 United States, Mountain View"`.
	 *
	 * The country code is converted to a Unicode flag emoji using Regional Indicator
	 * Symbol Letters (U+1F1E6–U+1F1FF) — no external library required.
	 * Parts that are `null` are omitted.
	 */
	fun format(result: GeoLocationResult?): String? =
		result?.run {
			val flag = countryCode
				?.takeIf { it.length == 2 && it.all(Char::isLetter) }
				?.uppercase()
				?.let { code ->
					code.map { c -> String(Character.toChars(0x1F1E6 - 'A'.code + c.code)) }
						.joinToString("")
				}
			val location = listOfNotNull(countryName, cityName).joinToString(", ")
			listOfNotNull(flag, location.takeIf { it.isNotEmpty() })
				.joinToString(" ")
				.takeIf { it.isNotEmpty() }
		}
}

/**
 * Geographic location derived from an IP address lookup.
 *
 * @property countryCode ISO 3166-1 alpha-2 country code (e.g. `"US"`)
 * @property countryName Human-readable country name (e.g. `"United States"`)
 * @property cityName    Human-readable city name (e.g. `"Mountain View"`)
 */
data class GeoLocationResult(
	val countryCode: String?,
	val countryName: String?,
	val cityName: String?,
)

