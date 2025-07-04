package com.rarchives.ripme.ripper.rippers;

import io.github.pixee.security.HostValidator;
import io.github.pixee.security.Urls;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Connection.Response;

import com.rarchives.ripme.utils.Http;

/*
 * Ripper for ArtStation's short URL domain.
 * Example URL: https://artstn.co/p/JlE15Z
 */

public class ArtstnRipper extends ArtStationRipper {
	public URL artStationUrl = null;

	public ArtstnRipper(URL url) throws IOException {
		super(url);
	}

	@Override
	public boolean canRip(URL url) {
		return url.getHost().endsWith("artstn.co");
	}

	@Override
	public String getGID(URL url) throws MalformedURLException {
		if (artStationUrl == null) {
			// Run only once.
			try {
				artStationUrl = getFinalUrl(url);
				if (artStationUrl == null) {
					throw new IOException("Null url received.");
				}
			} catch (IOException e) {
				LOGGER.error("Couldnt resolve URL.", e);
			}

		}
		return super.getGID(artStationUrl);
	}

	public URL getFinalUrl(URL url) throws IOException {
		if (url.getHost().endsWith("artstation.com")) {
			return url;
		}

		LOGGER.info("Checking url: " + url);
		Response response = Http.url(url).connection().followRedirects(false).execute();
		if (response.statusCode() / 100 == 3 && response.hasHeader("location")) {
			return getFinalUrl(Urls.create(response.header("location"), Urls.HTTP_PROTOCOLS, HostValidator.DENY_COMMON_INFRASTRUCTURE_TARGETS));
		} else {
			return null;
		}
	}
}
