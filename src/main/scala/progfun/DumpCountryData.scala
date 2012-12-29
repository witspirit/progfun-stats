package progfun

import data.Countries

object DumpCountryData extends App {
  println("Country Data:")
  Countries.countries.foreach(country =>
    println(country.iso+" | "+country.iso3+" | "+country.fullName+" | "+ (if (country.population.isEmpty) "Not Available" else country.population.get))
  )
}
