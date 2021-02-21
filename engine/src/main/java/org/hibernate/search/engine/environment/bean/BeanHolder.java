/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.engine.environment.bean;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An object holding a bean instance, and allowing to release it.
 *
 * @param <T> The type of the bean instance.
 */
public interface BeanHolder<T> extends AutoCloseable {

	/**
	 * @return The bean instance. Guaranteed to always return the exact same object,
	 * i.e. {@code beanHolder.get() == beanHolder.get()} is always true.
	 */
	T get();

	/**
	 * Release any resource currently held by the {@link BeanHolder}.
	 * <p>
	 * After this method has been called, the result of calling {@link #get()} on the same instance is undefined.
	 * <p>
	 * <strong>Warning</strong>: this method only releases resources that were allocated
	 * by the creator of the bean instance, and of which the bean instance itself may not be aware.
	 * If the bean instance itself (the one returned by {@link #get()}) exposes any {@code close()}
	 * or other release method, they should be called before the {@link BeanHolder} is released.
	 *
	 * @throws RuntimeException If an error occurs while releasing resources.
	 */
	@Override
	default void close() {
	};

	/**
	 * @param dependencies Dependencies that should be closed eventually.
	 * @return A bean holder that wraps the current bean holder, and ensures the dependencies are also
	 * closed when its {@link #close()} method is called.
	 */
	default BeanHolder<T> withDependencyAutoClosing(BeanHolder<?> ... dependencies) {
		return new DependencyClosingBeanHolder<>( this, Arrays.asList( dependencies ) );
	}

	/**
	 * Returns an empty {@code BeanHolder} instance. No value is present for this
	 * {@code BeanHolder}.
	 *
	 * @param <T> The type of the non-existent value
	 * @return an empty {@code BeanHolder}
	 */
	static <T> BeanHolder<T> empty() {
		@SuppressWarnings("unchecked")
		BeanHolder<T> t = () -> null;
		return t;
	}

	/**
	 * @param instance The bean instance.
	 * @param <T> The type of the bean instance.
	 * @return A {@link BeanHolder} whose {@link #get()} method returns the given instance,
	 * and whose {@link #close()} method does not do anything.
	 */
	static <T> BeanHolder<T> of(T instance) {
		return new SimpleBeanHolder<>( instance );
	}

	/**
	 * @param beanHolders The bean holders.
	 * @param <T> The type of the bean instances.
	 * @return A {@link BeanHolder} whose {@link #get()} method returns a list containing
	 * the instance of each given bean holder, in order,
	 * and whose {@link #close()} method closes every given bean holder.
	 */
	static <T> BeanHolder<List<T>> of(List<? extends BeanHolder<? extends T>> beanHolders) {
		return new CompositeBeanHolder<>( beanHolders );
	}

	/**
	 * Returns an {@code BeanHolder} describing the given value, if
	 * non-{@code null}, otherwise returns an empty {@code BeanHolder}.
	 *
	 * @param instance The bean instance.
	 * @param <T> The type of the bean instance.
	 * @return an {@code BeanHolder}  method returns the given instance,
	 * is non-{@code null}, otherwise an empty {@code BeanHolder}
	 */
	static <T> BeanHolder<T> ofNullable(T instance) {
		return instance == null ? empty() : of( instance );
	}

	/**
	 * If a istance is present, returns {@code true}, otherwise {@code false}.
	 *
	 * @return {@code true} if a value is present, otherwise {@code false}
	 */
	default boolean isPresent() {
		return get() != null;
	}

	/**
	 * If a istance is not present, returns {@code true}, otherwise
	 * {@code false}.
	 *
	 * @return {@code true} if a value is not present, otherwise {@code false}
	 * @since 11
	 */
	default boolean isEmpty() {
		return get() == null;
	}

	/**
	 * If a istance is present, performs the given action with the value,
	 * otherwise does nothing.
	 *
	 * @param <R>  the type of input to the {@code get()} function, and to the
         * composed function
	 * @param action the action to be performed, if a value is present
	 * @return {@code true} if a value is present, otherwise {@code false}
	 * @throws NullPointerException if value is present and the given action is
	 * {@code null}
	 */
	default <R extends T> R ifPresentGetAndMap(Function<? super T, R> action) {
		T value = get();
		if ( value != null ) {
			return action.apply( value );
		}
		return null;
	}

	/**
	 * If a istance is present, performs the given action with the value,
	 * otherwise performs the given empty-based action.
	 *
	 * @param <R>  the type of input to the {@code get()} function, and to the
         * composed function
	 * @param action the action to be performed, if a value is present
	 * @param emptyAction the empty-based action to be performed, if no value is
	 * present
	 * @return {@code true} if a value is present, otherwise {@code false}
	 * @throws NullPointerException if a value is present and the given action
	 * is {@code null}, or no value is present and the given empty-based
	 * action is {@code null}.
	 */
	default <R extends T> R ifPresentOrElseGetAndMap(Function<? super T, R> action, Supplier<R> emptyAction) {
		T value = get();
		if ( value != null ) {
			return action.apply( value );
		}
		else {
			return emptyAction.get();
		}
	}

}
