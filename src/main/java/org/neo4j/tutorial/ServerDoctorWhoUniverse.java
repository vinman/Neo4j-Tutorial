package org.neo4j.tutorial;

import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.neo4j.server.NeoServerWithEmbeddedWebServer;
import org.neo4j.server.rest.domain.JsonHelper;
import org.neo4j.server.rest.domain.JsonParseException;
import org.neo4j.tutorial.server.ServerBuilder;
import org.neo4j.tutorial.server.rest.FunctionalTestHelper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class ServerDoctorWhoUniverse {

	private final NeoServerWithEmbeddedWebServer server;

	public ServerDoctorWhoUniverse(DoctorWhoUniverseGenerator universe) throws Exception {
		super();
		server = ServerBuilder
				.server()
				.usingDatabaseDir(universe.getDatabaseDirectory())
				.build();
		server.start();
	}

	public Map<String,Object> theDoctor() {
		return getJsonFor(getUriFromIndex("characters", "name", "Doctor"));
	}
	
	public Map<String,Object> getJsonFor(String uri){
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource resource = client.resource(uri);
		String response = resource.accept(MediaType.APPLICATION_JSON).get(
				String.class);
		try {
			return JsonHelper.jsonToMap(response);
		} catch (JsonParseException e) {
			throw new RuntimeException("Invalid response when looking up Doctor node");
		}
	}
	
	public String getUriFromIndex(String indexName, String key, String value) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource resource = client.resource(new FunctionalTestHelper(server)
				.indexNodeUri(indexName, key, value));
		String response = resource.accept(MediaType.APPLICATION_JSON).get(
				String.class);
		try {
			return JsonHelper.jsonToList(response).get(0).get("self").toString();
		} catch (JsonParseException e) {
			throw new RuntimeException("Invalid response when looking up node");
		}
	}

	void stop() {
		server.stop();
	}

	public NeoServerWithEmbeddedWebServer getServer() {
		return server;
	}

}
