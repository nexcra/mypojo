/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package erwins.util.vender.tiles2;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.Attribute;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.TilesException;
import org.apache.tiles.access.TilesAccess;
import org.springframework.web.servlet.support.JstlUtils;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.util.WebUtils;

/**
 * 걍 일케 쓰는게 젤 나은듯
 * <p>View implementation that first checks if the request is a Tiles definition, then if it isn't tries to retrieve a default Tiles template and uses the url to dynamically set the body.</p>  최초로 Tiles를 찾는다. 만약 없다면 처음 박았던 디폴트 body를 사용한다. <p>The main template name defaults to 'mainTemplate', the body attribute defaults to to 'body', and the delimiter is '.' which is used for replacing a '/' in the url.</p> <p>If a request is made for '/program/index.html', Spring will pass 'info/index' into the view. The first thing will be to look for 'info/index' as a Tiles definition. Then a template definition of '.info.mainTemplate', which if found will dynamically have a body set on this definition. If the previous aren't found, it is assumed a root definition exists. This would be '.mainTemplate'. If none of these exist, a TilesException will be thrown.</p> <ol> <li>Check if a Tiles definition based on the URL matches.</li> <li>Checks if a definition derived from the URL and default template name exists and then dynamically insert the body based on the URL.</li> <li>Check if there is a root template definition and then dynamically insert the body based on the URL.</li> <li>If no match is found from any process above a TilesException is thrown.</li> </ol>
 * @author  David Winterfeldt
 * @author  David Winterfeldt가 만든것을 수정해서 사용.
 */
public abstract class AbstractDynamicTilesView extends AbstractUrlBasedView {
	
	/**
     * Keeps Tiles definition to use once derived.
     */
    private String derivedDefinitionName = null;

	/** 뭔지 모름 */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
    protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ServletContext servletContext = getServletContext();
        TilesContainer container = TilesAccess.getContainer(servletContext);
        
        if (container == null) { throw new ServletException("Tiles container is not initialized. "
                + "Have you added a TilesConfigurer to your web application context?"); }

        exposeModelAsRequestAttributes(model, request);
        renderMergedOutputModel(getBeanName(), getUrl(), servletContext, request, response, container);
    }
    

    /** 뭔지 모름 */
    protected void renderMergedOutputModel(String beanName, String url, ServletContext servletContext, HttpServletRequest request,
            HttpServletResponse response, TilesContainer container) throws Exception {
        
        JstlUtils.exposeLocalizationContext(new RequestContext(request, servletContext));

        if (!response.isCommitted()) {
            // Tiles is going to use a forward, but some web containers (e.g.
            // OC4J 10.1.3)
            // do not properly expose the Servlet 2.4 forward request
            // attributes... However,
            // must not do this on Servlet 2.5 or above, mainly for GlassFish
            // compatibility.
            if (servletContext.getMajorVersion() == 2 && servletContext.getMinorVersion() < 5) {
                WebUtils.exposeForwardRequestAttributes(request);
            }
        }

        String definitionName = startDynamicDefinition(beanName, url, request, response, container);

        container.render(definitionName, request, response);

        endDynamicDefinition(definitionName, beanName, request, response, container);
    }

    /** 뭔지 모름 */
    @SuppressWarnings("deprecation")
	protected String startDynamicDefinition(String beanName, String url, HttpServletRequest request, HttpServletResponse response,
            TilesContainer container) throws TilesException {
        
        String definitionName = processTilesDefinitionName(beanName, container, request, response);

        // create a temporary context and render using the incoming url as the
        // body attribute
        //만약 빈 이름과 definitionName이 일지하지 않는다면. 즉  동적으로 생성된거라면
        //definitionName에 동적으로 속성을 추가해 준다.
        //processTilesDefinitionName을 수정해서 리턴만 올바르다면 definitionName을 여러개 지정해도 상관 없다.
        if (!definitionName.equals(beanName)) {
            Attribute attr = new Attribute();
            attr.setName(getTilesBodyAttributeName());
            attr.setValue(url);

            AttributeContext attributeContext = container.startContext(request, response);
            attributeContext.putAttribute(getTilesBodyAttributeName(), attr);
            //logger.debug("URL used for Tiles body.  url='" + url + "'.");
        }

        return definitionName;
    }

    /** 뭔지 모름 */
    protected void endDynamicDefinition(String definitionName, String beanName, HttpServletRequest request, HttpServletResponse response,
            TilesContainer container) {
        if (!definitionName.equals(beanName)) {
            container.endContext(request, response);
        }
    }
    
    /** 프로젝트별로 달라지는 템플릿 정의 */
    protected abstract String definitionNameByProject(String beanName, TilesContainer container, HttpServletRequest request,HttpServletResponse response);
    
    /** 갈아끼우기 할 바디 설정 */
    protected abstract String getTilesBodyAttributeName();
    

    /**
     * Processes values to get tiles template definition name. First a Tiles
     * definition matching the url is checked, then a url specific template is
     * checked, and then just the default root definition is used.
     * 걍 플젝별로 위임
     * @throws TilesException
     *             If no valid Tiles definition is found.
     */
    protected String processTilesDefinitionName(String beanName, TilesContainer container, HttpServletRequest request, HttpServletResponse response) throws TilesException {
        // if definition already derived use it, otherwise 
        // check if url (bean name) is a template definition, then 
        // check for main template
        if (derivedDefinitionName != null) {  //캐싱되어 있다면
            return derivedDefinitionName;
        } else if (container.isValidDefinition(beanName, request, response)) {  //definition이 하드코딩된게 있다면 그걸 먼저 사용한다.
            derivedDefinitionName = beanName;
            return beanName;
        }

        derivedDefinitionName = definitionNameByProject(beanName,container,request,response); //캐싱한다~
        return derivedDefinitionName;
    }


}
