package org.jenkinsci.plugins.pitmutation.targets;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Ed Kimber
 */
public class MutationMetric<T, S> {
  public static MutationMetric<IntMetric, Integer> UNDETECTED =
          new MutationMetric<IntMetric, Integer>(new TypeToken<IntMetric>() {});
  public static MutationMetric<IntMetric, Integer> TOTAL_MUTATIONS =
          new MutationMetric<IntMetric, Integer>(new TypeToken<IntMetric>() {});
  public static MutationMetric<IntPercentMetric, IntPercentage> KILL_RATIO =
          new MutationMetric<IntPercentMetric, IntPercentage>(new TypeToken<IntPercentMetric>() {});

  public MutationMetric(TypeToken<T> typeToken) {
    type_ = typeToken;
  }

  public T createMetric(S value){
    try {
      return (T) type_.getRawType().getDeclaredConstructor(value.getClass()).newInstance(value);
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }

//  public TypeToken<T> getType() {
//    return type_;
//  }
//
//  public Class<T> getMetricClass() {
//    return metricClass_;
//  }

  private TypeToken<T> type_;
//  private Class<T> metricClass_;
}
