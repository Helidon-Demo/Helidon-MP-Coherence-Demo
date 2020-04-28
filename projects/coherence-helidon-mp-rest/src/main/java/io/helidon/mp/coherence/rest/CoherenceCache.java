/*
 * Copyright (c) 2018, 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.helidon.mp.coherence.rest;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.net.AsyncNamedCache;

import java.util.concurrent.CompletableFuture;
import java.util.Collection;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

// the class to provide a Coherence cache object
public class CoherenceCache{
	// the class provides a NameCache and a AsyncNamedCache object
	NamedCache<Object, Object> cache;
	AsyncNamedCache<Object, Object> asyncCache;

	// default constructor - create the CacheFactory and
	// use ensureCluster to create the Coherence instance
	public CoherenceCache() {
		CacheFactory.ensureCluster();
	}

	// constructor to create the actual cache
	//   input argument - key - the name of the Coherence cache
	public CoherenceCache(String cachename) {
		// create coherence instance
		CacheFactory.ensureCluster();
		// create the actual cache object for cachename
		this.cache = CacheFactory.getCache(cachename);
		this.asyncCache = this.cache.async();
	}

	// helper method getallKV()
	// return ALL K-V pairs of the cache as JsonArray
	public JsonArray getallKV() {
		// enclosing with try-catch
		try {
			// get all keys and store in keys
			Collection<Object> keys = cache.keySet();
			// create a blank json array to store the final result
			JsonArrayBuilder array = Json.createArrayBuilder();
			// loop for each key in cache and get the value
			for ( Object key : keys ) {
				// create a blank json object to store the K-V pair
				JsonObjectBuilder jsonresult = Json.createObjectBuilder();
				// get the value of a single key
				Object oValue = cache.get(key);
				// convert the K-V value to string
				String value = oValue.toString();
				String cacheKey = key.toString();
				// debug message can be comment out in prod
				System.out.println("------------- K-V : " + cacheKey + " : " + value );
				// construct the K-V as json
				jsonresult.add("key", cacheKey);
				jsonresult.add("value", value);
				JsonObject singleEntry = jsonresult.build();
				// and the json of k-v to the json array
				array.add(singleEntry);
			}
			// return the json array
			return array.build();
		} catch (Exception e) {
			// catching exception
			System.out.println("------------- exception!! : " + e );
		}
		// should not come here as the previous return should already
		// return the value in cache - just in case
		// we return something for the caller to handle
		try {
			return Json.createArrayBuilder()
					.add(Json.createObjectBuilder()
					.add("result", "no vaule in cache"))
					.build();
		} catch (Exception e) {
			// catching exception and print out error message
			System.out.println("------------- exception!! : " + e );
		}
		// should not come here as the previous return should already
		// return the value in cache - just in case
		// we return something for the caller to handle
		return Json.createArrayBuilder()
				.add(Json.createObjectBuilder()
				.add("result", "should not come here"))
				.build();
		} // end of helper method getallKV()

		// getoneKV() helper method
		// get a single V from the input K
		// output is json contains the K-V
	public JsonObject getoneKV(String key) {
		CompletableFuture<Object> myValue = this.asyncCache.get(key)
				.whenComplete((result,error) -> {
					// print out debug message - can remote later
					System.out.println("------------- result is: " + result.toString());
					System.out.println("------------- error is: " + error);
				});
		// using async method and we can slepp a while in main thread
		System.out.println("------------- program is sleeping... " );
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			System.out.println("------------- exception!! : " + e );
		}
		// construct the k-v result
		try {
			JsonObjectBuilder jsonresult = Json.createObjectBuilder();
			jsonresult.add("key",key);
			jsonresult.add("value",myValue.get().toString());
			// return the KV result
			return jsonresult.build();
		} catch (Exception e) {
			System.out.println("------------- exception!! : " + e );
		}
		// return key not found if cannot get the value
		return Json.createObjectBuilder()
				.add("result", "key not found").build();
	} // end of getoneKV() method

	// putoneKV() helper method
	// put one KV to cache using input K and V
	// output is json success message - async method
	public JsonObject putoneKV(String key, String theValue) {
		// perform am async put
		this.asyncCache.put(key, theValue)
			.whenComplete((result,error) -> {
				System.out.println("------------- result is: " + result.toString());
				System.out.println("------------- error is: " + error);
			});
		// aync method, sleep in the thread
		System.out.println("------------- program is sleeping... " );
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			System.out.println("------------- exception!! : " + e );
		}
		// return success
		return Json.createObjectBuilder()
				.add("result", "success").build();
	} // end of putoneKV() method

	// deleteoneKV() helper method
	// delete one KV from cache with input key
	// return success message
	public JsonObject deleteoneKV(String key) {
		this.asyncCache.remove(key)
			.whenComplete((result,error) -> {
				System.out.println("------------- result is: " + result.toString());
				System.out.println("------------- error is: " + error);
			});
		System.out.println("------------- program is sleeping... " );
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			System.out.println("------------- exception!! : " + e );
		}
		return Json.createObjectBuilder()
				.add("result", "success").build();
	}  // end of deleteoneKV()

	// deleteallKV() helper method
 	// delete all KV from cache
	// return success message
	public JsonObject deleteallKV() {
		// clear the async cache
		this.asyncCache.clear()
		.whenComplete((result,error) -> {
				System.out.println("------------- result is: " + result.toString());
				System.out.println("------------- error is: " + error);
			}
		);
		// sleep in thread and print out debug message
		System.out.println("------------- program is sleeping... " );
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			System.out.println("------------- exception!! : " + e );
		}
		// return success message
		return Json.createObjectBuilder()
				.add("result", "success").build();
	} // end of deleteallKV()

}