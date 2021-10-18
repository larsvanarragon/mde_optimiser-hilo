package nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.henshin.interpreter.Match;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.interpreter.impl.MatchImpl;
import org.eclipse.emf.henshin.interpreter.info.RuleChangeInfo;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

import nl.ru.icis.mdeoptimiser.hilo.encoding.model.Encoding;
import nl.ru.icis.mdeoptimiser.hilo.problems.cra.coupling.CRACoupleData;
import nl.ru.icis.mdeoptimiser.hilo.problems.cra.coupling.CRAEGraphImpl;

public class EncodingCRAVariation implements Variation {
  
  static EngineImpl engine = new EngineImpl();
  
  @Override
  public int getArity() {
    return 1;
  }

  @Override
  public Solution[] evolve(Solution[] parents) {
    // Copy and set parent
    Solution mutated = parents[0].copy();
    mutated.setAttribute("parent", parents[0]);
    
    // Get the variable we will mutate and set the current encoding
    EncodingCRAVariable toMutateVariable = (EncodingCRAVariable) mutated.getVariable(0);
    CRACoupleData.setCurrentEncoding(toMutateVariable.getEncoding());
    
    // Mutate it
    mutate(toMutateVariable);
    
    // Return it
    return new Solution[] {mutated};
  }
  
  // Mutate the variable with a random operator for a single step, if the operator fails we try another
  public void mutate(EncodingCRAVariable variable) {
    CRAEGraphImpl graph = new CRAEGraphImpl(variable.getModel(), variable.getEncoding());
    
    ArrayList<Unit> operators = new ArrayList<>(variable.getOperators());
    
    try {
      while (!operators.isEmpty()) {
        Collections.shuffle(operators);
        Rule rule = (Rule) operators.remove(0);
        
        if (rule.getName().equals("deleteEmptyClass") || rule.getName().equals("moveFeature")) {
          continue;
        }
        
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
    
    System.out.println(rule.getName());
    
    RuleChangeInfo ruleChange = engine.getRuleInfo(rule).getChangeInfo();
    
    for (Node node : ruleChange.getCreatedNodes()) {
      EObject createdObject = node.getType().getEPackage().getEFactoryInstance().create(node.getType());
      resultMatch.setNodeTarget(node, createdObject);
      
      encoding.addNewEObject(createdObject);
    }
    
    for (Node node : ruleChange.getDeletedNodes()) {
      System.out.println("Delete");
      // TODO mark object for possible deletion, check whether other objects have any reference to it
    }
    
    for (Node node : ruleChange.getPreservedNodes()) {
      Node lhsNode = rule.getMappings().getOrigin(node);
      resultMatch.setNodeTarget(node, match.getNodeTarget(lhsNode));
    }
    
    for (Edge edge : ruleChange.getCreatedEdges()) {
      EObject source = resultMatch.getNodeTarget(edge.getSource());
      EObject target = resultMatch.getNodeTarget(edge.getTarget());
     
      encoding.addRelationBetween(edge.getType(), source, target);
      
      // Fix the opposite
      EReference opposite = edge.getType().getEOpposite();
      if (opposite != null) {
        encoding.addRelationBetween(opposite, target, source);
      }
    }
    
    for (Edge edge : ruleChange.getDeletedEdges()) {
      EObject source = match.getNodeTarget(edge.getSource());
      EObject target = match.getNodeTarget(edge.getTarget());
      
      encoding.removeRelationBetween(edge.getType(), source, target);
      
      // Fix the opposite
      EReference opposite = edge.getType().getEOpposite();
      if (opposite != null) {
        encoding.removeRelationBetween(opposite, target, source);
      }
    }
    
    return true;
  }
}
