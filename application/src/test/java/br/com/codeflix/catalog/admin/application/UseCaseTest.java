package br.com.codeflix.catalog.admin.application;

import br.com.codeflix.catalog.admin.domain.Identifier;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.reset;

@ExtendWith(MockitoExtension.class)
public abstract class UseCaseTest implements BeforeEachCallback {

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        reset(getMocks().toArray());
    }

    protected abstract List<Object> getMocks();

    protected List<String> asString(final List<? extends Identifier> categories) {
        return categories.stream()
                .map(Identifier::getValue)
                .toList();
    }

    protected Set<String> asString(final Set<? extends Identifier> categories) {
        return categories.stream()
                .map(Identifier::getValue)
                .collect(Collectors.toSet());
    }
}