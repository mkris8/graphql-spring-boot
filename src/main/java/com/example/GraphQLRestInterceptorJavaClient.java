package com.example;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import graphql.GraphQL;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

public class GraphQLRestInterceptorJavaClient {

	public static void main(String args[]) {

		System.out.println(new GraphQLRestInterceptorJavaClient().invoke());
	}

	public Object invoke() {

		String line;
		
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
				// System.out.println(line);
				str += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		GraphQLObjectType userType = newObject().name("userType")
				.field(newFieldDefinition().name("id").type(GraphQLString))
				.field(newFieldDefinition().name("name").type(GraphQLString))
				.field(newFieldDefinition().name("email").type(GraphQLString)).build();

		System.out.println("Before builder :" + str.trim());

		JSONObject userJsonObject = new JSONObject();
		System.out.println("userJsonObject :"+ userJsonObject);
		
		try { 
			JSONParser parser = new JSONParser();
			userJsonObject = (JSONObject) parser.parse(str);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		GraphQLFieldDefinition.Builder userField = newFieldDefinition()
				.name("user")
				.type(userType)
				.staticValue(userJsonObject);

		GraphQLSchema graphQLSchema = GraphQLSchema.newSchema()
				.query(newObject()
						.name("RootQueryType")
						.field(userField)
						.build())
				.build();

		return GraphQL.newGraphQL(graphQLSchema).build().execute("{ user { id, name, email } }").getData();

	}

}
