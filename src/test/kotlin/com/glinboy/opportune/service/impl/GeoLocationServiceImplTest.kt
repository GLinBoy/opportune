package com.glinboy.opportune.service.impl

import com.maxmind.geoip2.DatabaseReader
import com.maxmind.geoip2.exception.AddressNotFoundException
import com.maxmind.geoip2.model.CityResponse
import com.maxmind.geoip2.record.City
import com.maxmind.geoip2.record.Country
import com.glinboy.opportune.service.GeoLocationResult
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.net.InetAddress

@ExtendWith(MockitoExtension::class)
class GeoLocationServiceImplTest {

	@Mock
	private lateinit var databaseReader: DatabaseReader

	// ---------- Happy path ----------

	@Test
	fun `lookup returns full result for a known public IP`() {
		val country = mock(Country::class.java)
		`when`(country.isoCode).thenReturn("US")
		`when`(country.name).thenReturn("United States")

		val city = mock(City::class.java)
		`when`(city.name).thenReturn("Mountain View")

		val cityResponse = mock(CityResponse::class.java)
		`when`(cityResponse.country).thenReturn(country)
		`when`(cityResponse.city).thenReturn(city)

		val ip = "8.8.8.8"
		`when`(databaseReader.city(InetAddress.getByName(ip))).thenReturn(cityResponse)

		val service = GeoLocationServiceImpl(databaseReader)
		val result = service.lookup(ip)

		assertNotNull(result)
		assertEquals("US", result!!.countryCode)
		assertEquals("United States", result.countryName)
		assertEquals("Mountain View", result.cityName)
	}

	@Test
	fun `lookup returns result with null city when city is unknown`() {
		val country = mock(Country::class.java)
		`when`(country.isoCode).thenReturn("DE")
		`when`(country.name).thenReturn("Germany")

		val city = mock(City::class.java)
		`when`(city.name).thenReturn(null)

		val cityResponse = mock(CityResponse::class.java)
		`when`(cityResponse.country).thenReturn(country)
		`when`(cityResponse.city).thenReturn(city)

		val ip = "1.2.3.4"
		`when`(databaseReader.city(InetAddress.getByName(ip))).thenReturn(cityResponse)

		val service = GeoLocationServiceImpl(databaseReader)
		val result = service.lookup(ip)

		assertNotNull(result)
		assertEquals("DE", result!!.countryCode)
		assertNull(result.cityName)
	}

	// ---------- Fallback: null reader ----------

	@Test
	fun `lookup returns null when DatabaseReader is null (disabled or missing)`() {
		val service = GeoLocationServiceImpl(null)
		assertNull(service.lookup("8.8.8.8"))
	}

	// ---------- Fallback: IP not in database ----------

	@Test
	fun `lookup returns null for private or loopback IP not found in database`() {
		val ip = "127.0.0.1"
		`when`(databaseReader.city(InetAddress.getByName(ip)))
			.thenThrow(AddressNotFoundException("Address not found in database"))

		val service = GeoLocationServiceImpl(databaseReader)
		assertNull(service.lookup(ip))
	}

	// ---------- Fallback: invalid IP ----------

	@Test
	fun `lookup returns null for an invalid IP string`() {
		val service = GeoLocationServiceImpl(databaseReader)
		assertNull(service.lookup("not-an-ip"))
	}

	// ---------- Fallback: unexpected exception ----------

	@Test
	fun `lookup returns null when DatabaseReader throws unexpected exception`() {
		val ip = "8.8.8.8"
		`when`(databaseReader.city(InetAddress.getByName(ip)))
			.thenThrow(RuntimeException("Unexpected I/O error"))

		val service = GeoLocationServiceImpl(databaseReader)
		assertNull(service.lookup(ip))
	}

	// ---------- format helper ----------

	@Test
	fun `format produces flag emoji followed by country name and city`() {
		val service = GeoLocationServiceImpl(null)
		val result = GeoLocationResult("US", "United States", "Mountain View")
		assertEquals("🇺🇸 United States, Mountain View", service.format(result))
	}

	@Test
	fun `format omits null city`() {
		val service = GeoLocationServiceImpl(null)
		val result = GeoLocationResult("FR", "France", null)
		assertEquals("🇫🇷 France", service.format(result))
	}

	@Test
	fun `format omits flag when country code is null`() {
		val service = GeoLocationServiceImpl(null)
		val result = GeoLocationResult(null, "Germany", "Berlin")
		assertEquals("Germany, Berlin", service.format(result))
	}

	@Test
	fun `format returns null for null GeoLocationResult`() {
		val service = GeoLocationServiceImpl(null)
		assertNull(service.format(null))
	}

	@Test
	fun `format returns null when all fields are null`() {
		val service = GeoLocationServiceImpl(null)
		val result = GeoLocationResult(null, null, null)
		assertNull(service.format(result))
	}
}

