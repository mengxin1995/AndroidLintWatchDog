package com.qjoy.JavaDetector;

import com.android.tools.lint.client.api.JavaParser;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;

import java.util.Collections;
import java.util.List;

import lombok.ast.AstVisitor;
import lombok.ast.ClassDeclaration;
import lombok.ast.ForwardingAstVisitor;
import lombok.ast.Node;

/**
 * Created by mengxin on 2018/2/8.
 */
public class AcitvityShouldAddRouteInspection extends Detector implements Detector.JavaScanner {

    public static final Issue ISSUE = Issue.create(
            "RouteInspection",
            "每个Activity需要增加路由支持",
            "使用@Route(path = 'xxx')",
            Category.CORRECTNESS, 9, Severity.ERROR,
            new Implementation(AcitvityShouldAddRouteInspection.class, Scope.JAVA_FILE_SCOPE));

    @Override
    public List<Class<? extends Node>> getApplicableNodeTypes() {
        return Collections.<Class<? extends Node>>singletonList(ClassDeclaration.class);
    }

    @Override
    public AstVisitor createJavaVisitor(final JavaContext context) {
        return new ForwardingAstVisitor() {
            public static final String Activity = "Activity";

            @Override
            public boolean visitClassDeclaration(ClassDeclaration node) {
                String name = node.astName().astValue();
                if(name.endsWith(Activity)){
                    JavaParser.ResolvedNode resolve = context.resolve(node);
                    if (resolve != null) {
                        boolean hasRoute = false;
                        for (JavaParser.ResolvedAnnotation annotation : resolve.getAnnotations()) {
                            if (annotation.getSignature().contains("com.alibaba.android.arouter.facade.annotation.Route")) {
                                hasRoute = true;
                                break;
                            }
                        }
                        if (!hasRoute) {
                            context.report(ISSUE, node, context.getLocation(node),
                                    "Activity上面需要加上路由");
                        }
                    }

                }
                return super.visitClassDeclaration(node);
            }
        };
    }
}
