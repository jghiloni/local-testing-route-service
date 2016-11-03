package com.ecsteam.cloudfoundry.routeservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Copyright 2016 ECS Team, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

@RestController
class RouteInterceptor {


	static final String FORWARDED_URL = "X-CF-Forwarded-Url";

	static final String PROXY_METADATA = "X-CF-Proxy-Metadata";

	static final String PROXY_SIGNATURE = "X-CF-Proxy-Signature";

	private final RestOperations restOperations;

	private final int servicePort;

	private final String serviceBasePath;

	@Autowired
	RouteInterceptor(RestOperations restOperations, @Value("${service.port:8080}") int servicePort,
		@Value("${service.context:}") String serviceBasePath) {
		this.restOperations = restOperations;
		this.servicePort = servicePort;
		this.serviceBasePath = serviceBasePath;
	}

	@RequestMapping(headers = {FORWARDED_URL, PROXY_METADATA, PROXY_SIGNATURE})
	public String service(RequestEntity<byte[]> incoming) throws URISyntaxException {

		RequestEntity<?> outgoing = getOutgoingRequest(incoming);

		ResponseEntity<byte[]> response = this.restOperations.exchange(outgoing, byte[].class);

		String x = new String(response.getBody());

		return x;
	}

	private RequestEntity<?> getOutgoingRequest(RequestEntity<?> incoming) throws URISyntaxException {
		HttpHeaders headers = new HttpHeaders();
		headers.putAll(incoming.getHeaders());

		URI requestedUri = headers.remove(FORWARDED_URL)
			.stream()
			.findFirst()
			.map(URI::create)
			.orElseThrow(() -> new IllegalStateException(String.format("No %s header present", FORWARDED_URL)));

		String host = "localhost";

		requestedUri = new URI("http",
			requestedUri.getRawUserInfo(),
			host,
			servicePort,
			serviceBasePath + requestedUri.getRawPath(),
			requestedUri.getRawQuery(),
			requestedUri.getRawFragment());

		System.out.println("Calling " + requestedUri.toString());

		return new RequestEntity<>(incoming.getBody(), headers, incoming.getMethod(), requestedUri);
	}
}
