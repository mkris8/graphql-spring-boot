/**
 *  Intercepts a webservice at http://localhost:8081/users using a graphql query.
 *  
 *  http://localhost:8081/users will return a resp as below (people.json needs to be amended with the below)
 *  {
		id: 1,
		name: "abc",
		email: "abc@gmail.com"
	}
 *  
 *   GraphQLRestRequestQueryProcessor generates response dynamically.
 
 	Postman can be used to fire graphql queries (GET requests) example :
 
 	http://localhost:8080/query/{user { id } }
 	http://localhost:8080/query/{user { id, name } }
 	http://localhost:8080/query/{user { id, name, email } }
 *  
 */

package com.example;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import graphql.GraphQL;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

/**
 * @author manojkrishnamurthy
 *
 */
@RestController
@EnableAutoConfiguration
public class GraphQLRestRequestQueryProcessor {

	private String line;
	private String queryFromRequest;
	
	@RequestMapping(value = "/query/{query}", method = RequestMethod.GET)
	public @ResponseBody Object invoke(@PathVariable("query") String query) {

		System.out.println("Query received from rqst : "+query);
		queryFromRequest = query;
		
		String str = invokeRestService();
		
		System.out.println("response from rest end point :" +str);

		JSONObject userJsonObject = converResponseToJSONObject(str);
		
		GraphQLObjectType userType = newObject().name("userType")
				.field(newFieldDefinition().name("id").type(GraphQLString))
				.field(newFieldDefinition().name("name").type(GraphQLString))
				.field(newFieldDefinition().name("email").type(GraphQLString)).build();

		GraphQLFieldDefinition.Builder userField = newFieldDefinition().
				name("user").
				type(userType).
				staticValue(userJsonObject);

		GraphQLSchema graphQLSchema = GraphQLSchema.newSchema()
				.query(newObject()
						.name("RootQueryType")
						.field(userField)
						.build())
				.build();


		// queryFromRequest is recieved via request using a client (for example postman eg : 
		// http://localhost:8080/query/{user { id } }
		// http://localhost:8080/query/{user { id, name } }
		// http://localhost:8080/query/{user { id, name, email } }
		
		System.out.println("Executing GrapgQL Query received from rqst : "+queryFromRequest);
		return GraphQL.newGraphQL(graphQLSchema).build().execute(queryFromRequest).getData();

	}

	private JSONObject converResponseToJSONObject(String str) {
		JSONObject userJsonObject = new JSONObject();

		try {
			JSONParser parser = new JSONParser();
			userJsonObject = (JSONObject) parser.parse(str);
			System.out.println("userJsonObject :"+ userJsonObject);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return userJsonObject;
	}

	private String invokeRestService() {
		// Invoke rest call
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet("http://localhost:8081/users");
		HttpResponse response;
		String str = "";
		try {
			response = client.execute(request);

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			line = "";
			while ((line = rd.readLine()) != null) {
				str += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
}
