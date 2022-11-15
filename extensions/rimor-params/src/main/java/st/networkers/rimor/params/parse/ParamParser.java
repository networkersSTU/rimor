package st.networkers.rimor.params.parse;

import st.networkers.rimor.context.ExecutionContext;
import st.networkers.rimor.inject.Injector;
import st.networkers.rimor.inject.Token;
import st.networkers.rimor.params.Params;
import st.networkers.rimor.params.parse.builtin.BooleanParamParser;
import st.networkers.rimor.params.parse.builtin.EnumParamParser;
import st.networkers.rimor.params.parse.builtin.PresentObjectParamParser;

/**
 * Abstract class for providers that just parse an element from the {@link Params}-annotated lists.
 *
 * @see AbstractParamParser
 * @see BooleanParamParser
 * @see EnumParamParser
 * @see PresentObjectParamParser
 */
public interface ParamParser<T> {

    Object getParameter(Token<T> token, Injector injector, ExecutionContext context);

    T parse(Object parameter, Token<T> token, Injector injector, ExecutionContext context);

}
