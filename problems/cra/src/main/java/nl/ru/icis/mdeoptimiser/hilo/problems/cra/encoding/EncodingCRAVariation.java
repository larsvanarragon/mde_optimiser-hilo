package nl.ru.icis.mdeoptimiser.hilo.problems.cra.encoding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.interpreter.impl.UnitApplicationImpl;
import org.eclipse.emf.henshin.model.ParameterKind;
import org.eclipse.emf.henshin.model.Unit;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

import models.cra.fitness.architectureCRA.ClassModel;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.henshin.MdeoRuleApplicationImpl;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.henshin.MdeoUnitApplicationImpl;

public class EncodingCRAVariation implements Variation {
  
  Engine engine = new EngineImpl();

  MdeoRuleApplicationImpl unitRunner = new MdeoRuleApplicationImpl(engine);
  
  
  @Override
  public int getArity() {
    return 1;
  }

  @Override
  public Solution[] evolve(Solution[] parents) {
    EncodingCRAVariable variable = (EncodingCRAVariable) parents[0].getVariable(0);
//    EGraphImpl graph = new EGraphImpl(variable.getModel());
//
//    
//    unitRunner.setEGraph(graph);
//    unitRunner.setUnit(operator);
    
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

    // Run the selected Henshin Unit
    unitRunner.execute(null);
    
    return parents;
  }
  
  // To be used in the EncodingCRAVariable simulating MDEOptimiser's initial mutation
  public void mutate(EncodingCRAVariable variable) {
    EGraphImpl graph = new EGraphImpl(variable.getModel());
    
    ArrayList<Unit> operators = new ArrayList<>(variable.getOperators());
    Collections.shuffle(operators);
    
    for (Unit operator : operators) {
      
    }
  }

}
