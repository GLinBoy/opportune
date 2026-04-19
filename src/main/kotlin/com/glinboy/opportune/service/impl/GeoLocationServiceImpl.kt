package com.glinboy.opportune.service.impl

import com.maxmind.geoip2.DatabaseReader
import com.maxmind.geoip2.exception.AddressNotFoundException
import com.glinboy.opportune.service.GeoLocationResult
import com.glinboy.opportune.service.GeoLocationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.InetAddress
import java.net.UnknownHostException

/**
 * Offline IP geolocation backed by the MaxMind GeoLite2-City database.
 *
 * The injected [DatabaseReader] is nullable: when the database file is absent or
 * geolocation is disabled, [lookup] simply returns `null` without throwing.
 */
@Service
class GeoLocationServiceImpl(
	private val reader: DatabaseReader?
) : GeoLocationService {

	private val log = LoggerFactory.getLogger(this::class.java)

	/**
	 * Looks up country and city information for the given [ip] address.
	 *
	 * Returns `null` for:
	 * - loopback / private addresses (best-effort: MaxMind returns [AddressNotFoundException])
	 * - IPs not present in the database
	 * - any parsing or I/O error
	 * - when the database reader is unavailable
	 */
	override fun lookup(ip: String): GeoLocationResult? {
		val dbReader = reader ?: return null

		val address: InetAddress = try {
			InetAddress.getByName(ip)
		} catch (e: UnknownHostException) {
			log.debug("Cannot resolve IP address '{}': {}", ip, e.message)
			return null
		}

		return try {
			val response = dbReader.city(address)
			GeoLocationResult(
				countryCode = response.country.isoCode,
				countryName = response.country.name,
				cityName    = response.city.name
			)
		} catch (e: AddressNotFoundException) {
			log.debug("IP address '{}' not found in GeoLite2 database", ip)
			null
		} catch (e: Exception) {
			log.warn("GeoIP lookup failed for IP '{}': {}", ip, e.message)
			null
		}
	}
}

