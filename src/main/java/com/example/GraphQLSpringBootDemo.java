
/**
 * 
 */
package com.example;

import java.util.Map;
import org.json.simple.JSONObject;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//graphql-java imports

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
public class GraphQLSpringBootDemo {

	// Enable GraphiQL Tool
	// Tool becomes accessible at the root /
	// http://localhost:8080/

	
	// A simple request mapping
	
	@RequestMapping("/init")
	String home() {
		return "GraphQL spring boot starter initiated...";
	}
	
	
	// A simple hello world
	
	@RequestMapping("/hello")
	Map<String, Object> hello() {
		GraphQLObjectType queryType = newObject()
				.name("helloWorldQuery")
				.field(newFieldDefinition()
						.type(GraphQLString)
						.name("hello")
						.staticValue("world"))
				.build();

		GraphQLSchema schema = GraphQLSchema.newSchema().query(queryType).build();

		GraphQL graphQL = GraphQL.newGraphQL(schema).build();

		Map<String, Object> result = graphQL.execute("{hello}").getData();
		System.out.println(result);

		return result;
	}
	
	
	@RequestMapping("/simpson")
	Object createSimpson() {
		
				// Create a object type
		
				GraphQLObjectType simpsonType = newObject()
		                .name("simpsonType")
		                .field(
		                newFieldDefinition()
		                        .name("id")
		                        .type(GraphQLString))
		                .field(
		                newFieldDefinition()
		                        .name("name")
		                        .type(GraphQLString))
		                .build();
				
				// Create a hardcoded JSON response object eg : {id=123, name=homer}
			 	
				JSONObject simpsonJSONObject = new JSONObject();
				simpsonJSONObject.put("id","123");
				simpsonJSONObject.put("name", "homer");
			 
				
				// field definitions for the object type
				
		        GraphQLFieldDefinition.Builder simpsonField = newFieldDefinition()
		                .name("simpson")
		                .type(simpsonType)
		            //  .staticValue("[id: '123', name: 'homer']"); // This will only return null
		                .staticValue(simpsonJSONObject); // hence a proper json obj has to be passed.
		              

		        // create the schema
		        
		        GraphQLSchema graphQLSchema = GraphQLSchema.newSchema().query(
		                newObject()
		                        .name("RootQueryType")
		                        .field(simpsonField)
		                        .build()
		        ).build();
			
		       String requestQuery_id = "{ simpson { id } }"; // returns {simpson={id=123}}
		       String requestQuery_id_name = "{ simpson { id, name } }"; // returns {simpson={id=123, name=homer}}
		       String requestQuery_name = "{ simpson { name } }";// returns {simpson={name=homer}}
		       
		       //execute the query
		       
		       return GraphQL.newGraphQL(graphQLSchema).build().execute(requestQuery_id_name).getData();
		
	}
	
}