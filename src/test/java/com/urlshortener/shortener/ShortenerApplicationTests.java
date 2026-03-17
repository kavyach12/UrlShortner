package com.urlshortener.shortener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ShortenerApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void browserGetShortenEndpointCreatesShortUrlAndRedirects() throws Exception {
		String shortUrl = mockMvc.perform(get("/shorten").param("url", "https://google.com"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Type", "text/plain;charset=UTF-8"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string(endsWith("/b")))
				.andReturn()
				.getResponse()
				.getContentAsString();

		String code = shortUrl.substring(shortUrl.lastIndexOf('/') + 1);

		mockMvc.perform(get("/" + code))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("https://google.com"));
	}

}
