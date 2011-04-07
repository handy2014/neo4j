package org.neo4j.server.rest.repr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.neo4j.server.rest.repr.ObjectRepresentation;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jVertex;
import com.tinkerpop.gremlin.pipes.GremlinPipeline;
import com.tinkerpop.pipes.Pipe;

public class PipeRepresentation extends ObjectRepresentation {
	
	private final ArrayList<Vertex> pipeOfVertices;

    public PipeRepresentation( ArrayList<Vertex> pipeOfVertices )
    {
        super( RepresentationType.NODE );
        this.pipeOfVertices = pipeOfVertices;
    }

    @Mapping("self")
    public ValueRepresentation selfUri() {
        LinkedHashMap<String,Object> curVertexMap = new LinkedHashMap<String,Object>();
    	Neo4jVertex curVertex = null;
    	String curVertexName="";
    	String curKey="";
        GremlinPipeline<Vertex,Edge> curPipeline=(GremlinPipeline<Vertex, Edge>) pipeOfVertices.get(0);
        Iterator<Edge> curItr=curPipeline.iterator();
        while (curItr.hasNext())
        {
        	curVertex=(Neo4jVertex) curItr.next();
        	Iterator<String> curKeySet=curVertex.getPropertyKeys().iterator();
        	while (curKeySet.hasNext())
        	{
        		curKey=curKeySet.next();
        		if (curKey.equalsIgnoreCase("name"))
        		{
        			curVertexName=(String) curVertex.getProperty(curKey);
        			curVertexMap.put(curVertexName, curVertex);
        			break;
        		}
        	}
        	
        }

        return ValueRepresentation.string( "vertices: " + curVertexMap );
    }
}
