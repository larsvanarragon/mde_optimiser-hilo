package nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.Match;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.interpreter.impl.MatchImpl;
import org.eclipse.emf.henshin.interpreter.info.RuleChangeInfo;
import org.eclipse.emf.henshin.interpreter.info.RuleInfo;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Parameter;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

import nl.ru.icis.mdeoptimiser.hilo.encoding.exception.DuplicateIdentifierEObjectPairException;
import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Encoding;
import nl.ru.icis.mdeoptimiser.hilo.problems.cra.coupling.CRAEGraphImpl;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.henshin.MdeoRuleApplicationImpl;

public class EncodingCRAVariation implements Variation {
  
  static EngineImpl engine = new EngineImpl();

  MdeoRuleApplicationImpl ruleRunner = new MdeoRuleApplicationImpl(engine);
  
  
  @Override
  public int getArity() {
    return 1;
  }

  @Override
  public Solution[] evolve(Solution[] parents) {
    EncodingCRAVariable variable = (EncodingCRAVariable) parents[0].getVariable(0);
    
    mutate(variable);
    
    return parents;
  }
  
  // TODO we want to instead of henshin applying the rule only match it
  
  // Mutate the variable with a random operator for a single step, if the operator fails we try another
  public void mutate(EncodingCRAVariable variable) {
    CRAEGraphImpl graph = new CRAEGraphImpl(variable.getModel(), variable.getEncoding());
    ruleRunner.setEGraph(graph);
    
    ArrayList<Unit> operators = new ArrayList<>(variable.getOperators());
    
    try {
      while (!operators.isEmpty()) {
        Collections.shuffle(operators);
        Rule rule = (Rule) operators.remove(0);
        
        Match potentialMatch = engine.findMatches(rule, graph, null).iterator().next();
        if (mutateEncodingWithMatch(variable.getEncoding(), potentialMatch, new MatchImpl(rule, true), rule)) {
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    
  }
  
  private boolean mutateEncodingWithMatch(Encoding encoding, Match match, Match resultMatch, Rule rule) throws Exception {
    if (match == null) {
      // No Match given
      return false;
    }
    
    if (!match.isValid() || !match.isComplete()) {
      // Not a valid or complete match
      return false;
    }
    
    RuleChangeInfo ruleChange = engine.getRuleInfo(rule).getChangeInfo();
    
    for (Node node : ruleChange.getCreatedNodes()) {
      EObject createdObject = node.getType().getEPackage().getEFactoryInstance().create(node.getType());
      resultMatch.setNodeTarget(node, createdObject);
      
      encoding.addNewEObject(createdObject);
    }
    
    for (Node node : ruleChange.getDeletedNodes()) {
      // TODO mark object for possible deletion, check whether other objects have any reference to it
    }
    
    for (Node node : ruleChange.getPreservedNodes()) {
      Node lhsNode = rule.getMappings().getOrigin(node);
      resultMatch.setNodeTarget(node, match.getNodeTarget(lhsNode));
    }
    
    for (Edge edge : ruleChange.getCreatedEdges()) {
      EObject source = resultMatch.getNodeTarget(edge.getSource());
      EObject target = resultMatch.getNodeTarget(edge.getTarget());
      
      String sourceRelationIdentifier = source.eClass().getEPackage().getName() + source.eClass().getName();
      String targetRelationIdentifier = edge.getType().getEGenericType().getERawType().getEPackage().getName() + edge.getType().getEGenericType().getERawType().getName();
      encoding.addRelationBetween(edge.getType().getName(), source, sourceRelationIdentifier, target, targetRelationIdentifier);
    }
    
    for (Edge edge : ruleChange.getDeletedEdges()) {
      EObject source = match.getNodeTarget(edge.getSource());
      EObject target = match.getNodeTarget(edge.getTarget());

      String sourceRelationIdentifier = source.eClass().getEPackage().getName() + source.eClass().getName();
      String targetRelationIdentifier = edge.getType().getEGenericType().getERawType().getEPackage().getName() + edge.getType().getEGenericType().getERawType().getName();
      encoding.removeRelationBetween(edge.getType().getName(), source, sourceRelationIdentifier, target, targetRelationIdentifier);
    }
    
    return true;
  }

}
