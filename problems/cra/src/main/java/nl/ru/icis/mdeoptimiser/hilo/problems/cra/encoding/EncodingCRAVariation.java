package nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.model.Unit;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

import nl.ru.icis.mdeoptimiser.hilo.problems.cra.coupling.CRAEGraphImpl;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.henshin.MdeoRuleApplicationImpl;

public class EncodingCRAVariation implements Variation {
  
  static Engine engine = new EngineImpl();

  MdeoRuleApplicationImpl ruleRunner = new MdeoRuleApplicationImpl(engine);
  
  
  @Override
  public int getArity() {
    return 1;
  }

  @Override
  public Solution[] evolve(Solution[] parents) {
    EncodingCRAVariable variable = (EncodingCRAVariable) parents[0].getVariable(0);
    
    mutate(variable);
    
//    var inParameters =
//        operator.getParameters().stream()
//            .filter(parameter -> parameter.getKind().equals(ParameterKind.IN))
//            .collect(Collectors.toList());

//    if (!inParameters.isEmpty()) {
//      inParameters.forEach(
//          parameter ->
//              unitRunner.setParameterValue(
//                  parameter.getName(),
//                  evolverParametersFactory.getParameterValue(operator, parameter, object)));
//    }
    
    return parents;
  }
  
  // TODO we want to instead of henshin applying the rule only match it
  
  // Mutate the variable with a random operator for a single step, if the operator fails we try another
  public void mutate(EncodingCRAVariable variable) {
    CRAEGraphImpl graph = new CRAEGraphImpl(variable.getModel(), variable.getEncoding());
    ruleRunner.setEGraph(graph);
    
    ArrayList<Unit> operators = new ArrayList<>(variable.getOperators());
    
    while (!operators.isEmpty()) {
      Collections.shuffle(operators);
      ruleRunner.setUnit(operators.remove(0));
      
      if (ruleRunner.execute(null)) {
        break;
      }
    }
  }

}
