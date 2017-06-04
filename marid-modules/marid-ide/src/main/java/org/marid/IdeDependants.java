/*
 * Copyright (c) 2016 Dmitry Ovchinnikov
 * Marid, the free data acquisition and visualization software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.marid;

import javafx.application.Platform;
import javafx.scene.control.Dialog;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Tab;
import javafx.stage.Window;
import org.marid.ide.event.PropagatedEvent;
import org.marid.ide.tabs.IdeTab;
import org.marid.misc.Builder;
import org.marid.spring.dependant.DependantConfiguration;
import org.marid.spring.postprocessors.IdeAutowirePostProcessor;
import org.marid.spring.postprocessors.MaridCommonPostProcessor;
import org.marid.spring.postprocessors.WindowAndDialogPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * @author Dmitry Ovchinnikov
 */
@Component("dependants")
public class IdeDependants {

    private static final Collection<WeakReference<GenericApplicationContext>> CONTEXTS = new ConcurrentLinkedQueue<>();

    private final GenericApplicationContext parent;

    @Autowired
    public IdeDependants(GenericApplicationContext parent) {
        this.parent = parent;
    }

    public GenericApplicationContext start(Consumer<AnnotationConfigApplicationContext> consumer) {
        final AnnotationConfigApplicationContext context = new DependantContext(parent);
        consumer.accept(context);
        context.refresh();
        context.start();
        return context;
    }

    public GenericApplicationContext start(Class<?> conf, Consumer<AnnotationConfigApplicationContext> consumer) {
        return start(context -> {
            context.register(conf);
            consumer.accept(context);
        });
    }

    public <T> GenericApplicationContext start(Class<? extends DependantConfiguration<T>> conf, T param, Consumer<AnnotationConfigApplicationContext> consumer) {
        return CONTEXTS.stream()
                .map(Reference::get)
                .filter(Objects::nonNull)
                .filter(c -> {
                    if (c.containsBean("params")) {
                        final Object params = c.getBean("params");
                        return params.equals(param);
                    } else {
                        return false;
                    }
                })
                .findAny()
                .map(IdeDependants::activate)
                .orElseGet(() -> start(context -> {
                    context.getBeanFactory().registerSingleton("params", param);
                    context.register(conf);
                    consumer.accept(context);
                }));
    }

    private static GenericApplicationContext activate(GenericApplicationContext context) {
        context.getBeansOfType(IdeTab.class, false, false).forEach((name, tab) -> {
            final SelectionModel<Tab> selectionModel = tab.getTabPane().getSelectionModel();
            selectionModel.select(tab);
        });
        context.getBeansOfType(Window.class, false, false).forEach((name, win) -> win.requestFocus());
        context.getBeansOfType(Dialog.class, false, false).forEach((name, win) -> win.show());
        return context;
    }

    @Override
    public String toString() {
        return parent.toString();
    }

    private static class DependantContext extends AnnotationConfigApplicationContext {

        private final ParentListener parentListener;

        private DependantContext(GenericApplicationContext parent) {
            super(Builder.build(new DefaultListableBeanFactory(), IdeAutowirePostProcessor::register));
            parent.addApplicationListener(parentListener = new ParentListener(this, parent));
            setAllowBeanDefinitionOverriding(false);
            setAllowCircularReferences(false);
            getBeanFactory().addBeanPostProcessor(new WindowAndDialogPostProcessor(this));
            getBeanFactory().addBeanPostProcessor(new MaridCommonPostProcessor());
            getBeanFactory().setParentBeanFactory(parent.getDefaultListableBeanFactory());
            register(IdeDependants.class);
        }

        @Override
        protected void onRefresh() throws BeansException {
            parentListener.close();
            CONTEXTS.removeIf(c -> c.get() == null);
            CONTEXTS.add(new WeakReference<>(this));
        }

        @Override
        protected void onClose() {
            CONTEXTS.removeIf(c -> c.get() == null || c.get() == this);
            for (final WeakReference<GenericApplicationContext> ref : CONTEXTS) {
                final GenericApplicationContext c = ref.get();
                if (c != null && c.getBeanFactory().getParentBeanFactory() == getBeanFactory()) {
                    c.close();
                    return;
                }
            }
        }

        @Override
        protected void finalize() throws Throwable {
            Platform.runLater(this::close);
        }

        private static class ParentListener extends WeakReference<GenericApplicationContext>
                implements ApplicationListener<ApplicationEvent>, AutoCloseable {

            private final GenericApplicationContext parent;

            private ParentListener(GenericApplicationContext referent, GenericApplicationContext parent) {
                super(referent);
                this.parent = parent;
            }

            @Override
            public void onApplicationEvent(ApplicationEvent event) {
                final GenericApplicationContext context = get();
                if (context == null || !context.isActive()) {
                    close();
                } else {
                    if (event instanceof ContextClosedEvent) {
                        context.close();
                    } else if (event instanceof PropagatedEvent) {
                        context.publishEvent(event);
                    }
                }
            }

            @Override
            public void close() {
                parent.getApplicationListeners().remove(this);
            }
        }
    }
}
