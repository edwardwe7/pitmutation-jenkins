package org.jenkinsci.plugins.pitmutation.metrics;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Ed Kimber
 */
public class MutationMetric<T extends Metric<S>, S> {
  public static MutationMetric<IntMetric, Integer> UNDETECTED =
          new MutationMetric<IntMetric, Integer>("Undetected" , IntMetric.class);
  public static MutationMetric<IntMetric, Integer> TOTAL_MUTATIONS =
          new MutationMetric<IntMetric, Integer>("Total", IntMetric.class);
  public static MutationMetric<IntPercentMetric, IntPercentage> KILL_RATIO =
          new MutationMetric<IntPercentMetric, IntPercentage>("Coverage", IntPercentMetric.class);

  public MutationMetric(String name, Class<T> cls) {
    name_ = name;
    type_ = TypeToken.of(cls);
  }

  public String getName() {
    return name_;
  }

  public T createMetric(S value){
    try {
      return type_.constructor(type_.getRawType().getDeclaredConstructor(value.getClass())).invoke(null, value);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }

  private TypeToken<T> type_;
  private String name_;

}
