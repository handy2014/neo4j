package org.neo4j.server.plugin.gremlin;

import java.util.ArrayList;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.server.plugins.Description;
import org.neo4j.server.plugins.Name;
import org.neo4j.server.plugins.Parameter;
import org.neo4j.server.plugins.PluginTarget;
import org.neo4j.server.plugins.ServerPlugin;
import org.neo4j.server.plugins.Source;
import org.neo4j.server.rest.repr.EdgeRepresentation;
import org.neo4j.server.rest.repr.ListRepresentation;
import org.neo4j.server.rest.repr.PipeRepresentation;
import org.neo4j.server.rest.repr.Representation;
import org.neo4j.server.rest.repr.RepresentationType;
import org.neo4j.server.rest.repr.VertexRepresentation;

import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;

/* This is a class that will represent a server side
 * gremlin plugin and will run through and return json
 * for the following use cases:
 * Add/delete vertices and edges from the graph.
 * Manipulate the graph indices.
 * Search for elements of a graph.
 * Load graph data from a file or URL.
 * Make use of JUNG algorithms.
 * Make use of SPARQL queries over OpenRDF-based graphs.
 * and much, much more.
 */

@Description( "A server side gremlin plugin for the neo4j REST server that will perform various node traversals and searches" )
public class GremlinPlugin extends ServerPlugin
{
    /**
     * This will give a plugin of type
     * curl -d 'script=results.add(start)' http://localhost:7474/db/data/ext/GremlinPlugin/node/0/execute_from_node
     * 
     * @param startNode
     * @param script
     * @return
     */
    @Name( "execute_from_node" )
    @Description( "execute a Gremlin script with variables 'start' set to the start node 'g' set to the Neo4jGraph and 'results' containing a resulting vertex" )
    @PluginTarget( Node.class )
    public VertexRepresentation getVertex(
            @Source Node startNode,
            @Description( "The Gremlin script" ) @Parameter( name = "script", optional=true ) String script )
    {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName( "gremlin" );
        ArrayList results = new ArrayList();
        Neo4jGraph graph = new Neo4jGraph( startNode.getGraphDatabase() );
        engine.getBindings( ScriptContext.ENGINE_SCOPE ).put( "g",
                graph );
        engine.getBindings( ScriptContext.ENGINE_SCOPE ).put( "start",
                graph.getVertex( startNode.getId() ));
        engine.getBindings( ScriptContext.GLOBAL_SCOPE ).put( "results",
                results );
        try
        {
            engine.eval( script );
        }
        catch ( ScriptException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new VertexRepresentation((Vertex) results.get( 0 ));
    }
    
    
    
    /**
     * This will give a plugin of type
     * curl -d 'script=g.E' http://localhost:7474/db/data/ext/GremlinPlugin/graphDb/get_all_edges
     * which should return an arrayList consisting of all the edges of the current graph
     * @param startNode
     * @param script
     * @return
     */
    @Name( "get_all_edges" )
    @Description( "execute a Gremlin script with variables 'start' set to the start node 'g' set to the Neo4jGraph and 'results' containing a resulting vertex" )
    @PluginTarget( GraphDatabaseService.class )
    public Representation getEdges(
            @Description( "The Gremlin script" ) @Parameter( name = "script", optional=true ) String script ,@Source GraphDatabaseService graphDb)
    {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName( "gremlin" );
        ArrayList<EdgeRepresentation> results = new ArrayList<EdgeRepresentation>();
        Neo4jGraph graph = new Neo4jGraph( graphDb );
        engine.getBindings( ScriptContext.ENGINE_SCOPE ).put( "g",
                graph );
        engine.getBindings( ScriptContext.GLOBAL_SCOPE ).put( "results",
                results );
        try
        {
            engine.eval( script );
        }
        catch ( ScriptException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new ListRepresentation( RepresentationType.RELATIONSHIP, results );
    }
    
    
    
    
    /**
     * This will give a plugin of type
     * curl -d 'script=g.V' http://localhost:7474/db/data/ext/GremlinPlugin/graphDb/get_all_vertexes
     * which should return an arrayList consisting of all the vertexes of the current graph
     * @param startNode
     * @param script
     * @return
     */
    @Name( "get_all_vertexes" )
    @Description( "execute a Gremlin script with variables 'start' set to the start node 'g' set to the Neo4jGraph and 'results' containing the set of resulting vertices" )
    @PluginTarget( GraphDatabaseService.class )
    public Representation getVertices(
            @Description( "The Gremlin script" ) @Parameter( name = "script", optional=true ) String script ,@Source GraphDatabaseService graphDb)
    {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName( "gremlin" );
        ArrayList<Vertex> results = new ArrayList<Vertex>();
        Neo4jGraph graph = new Neo4jGraph( graphDb );
        engine.getBindings( ScriptContext.ENGINE_SCOPE ).put( "g",
                graph );
        engine.getBindings( ScriptContext.GLOBAL_SCOPE ).put( "results",
                results );
        try
        {
            engine.eval( script );
        }
        catch ( ScriptException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Representation resultRep = new PipeRepresentation( results );
        return resultRep;
    }
    
}
