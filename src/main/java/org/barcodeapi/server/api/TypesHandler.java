package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.gen.CodeType;
import org.json.JSONArray;
import org.json.JSONException;

public class TypesHandler extends RestHandler {

	public TypesHandler() {
		super();
	}

	@Override
	protected void onRequest(String uri, HttpServletRequest request, HttpServletResponse response)
			throws JSONException, IOException {

		// loop all supported types
		JSONArray output = new JSONArray();
		for (CodeType type : CodeType.values()) {
			output.put(type.toJSON());
		}

		// print response to client
		response.getOutputStream().println(output.toString(4));
	}
}
