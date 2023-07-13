package st.networkers.rimor.params;

import st.networkers.rimor.Rimor;
import st.networkers.rimor.extension.RimorExtension;
import st.networkers.rimor.params.parse.support.BooleanInstructionParamParser;
import st.networkers.rimor.params.parse.support.DefaultInstructionParamParser;
import st.networkers.rimor.params.parse.support.EnumInstructionParamParser;
import st.networkers.rimor.params.parse.support.StringInstructionParamParser;

public class ParamsExtension implements RimorExtension {

    @Override
    public void configure(Rimor rimor) {
        rimor.registerProvider(new BooleanInstructionParamParser())
                .registerProvider(new DefaultInstructionParamParser())
                .registerProvider(new EnumInstructionParamParser())
                .registerProvider(new StringInstructionParamParser());
    }
}
