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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.JstlUtils;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.util.WebUtils;

import erwins.util.lib.Strings;

/**
 * <p>Used for rendering and processing a dynamic tiles view.</p>
 * @author  David Winterfeldt
 * @author  David Winterfeldt가 만든것을 수정해서 사용.
 */
public class CopyOfDynamicTilesViewProcessor {
    
    /** 이 필터를 조합해서 사용한다. */
    private static final String[] FILTER = {"_","$"};

    final Logger logger = LoggerFactory.getLogger(CopyOfDynamicTilesViewProcessor.class);

    /**
     * Keeps Tiles definition to use once derived.
     */
    private String derivedDefinitionName = null;

    private String tilesDefinitionName = "mainTemplate";
    private String tilesBodyAttributeName = "content";
    private String tilesDefinitionDelimiter = ".";

    /**
     * Main template name. The default is 'mainTemplate'.
     * @param tilesDefinitionName  Main template name used to lookup definitions.
     * @uml.property  name="tilesDefinitionName"
     */
    public void setTilesDefinitionName(String tilesDefinitionName) {
        this.tilesDefinitionName = tilesDefinitionName;
    }

    /**
     * Tiles body attribute name. The default is 'body'.
     * @param tilesBodyAttributeName  Tiles body attribute name.
     * @uml.property  name="tilesBodyAttributeName"
     */
    public void setTilesBodyAttributeName(String tilesBodyAttributeName) {
        this.tilesBodyAttributeName = tilesBodyAttributeName;
    }

    /**
     * Sets Tiles definition delimiter. For example, instead of using the request 'info/about' to lookup the template definition 'info/mainTemplate', the default delimiter of '.' would look for '.info.mainTemplate'
     * @param tilesDefinitionDelimiter  Optional delimiter to replace '/' in a url.
     * @uml.property  name="tilesDefinitionDelimiter"
     */
    public void setTilesDefinitionDelimiter(String tilesDefinitionDelimiter) {
        this.tilesDefinitionDelimiter = tilesDefinitionDelimiter;
    }

    /**
     * Renders output using Tiles.
     */
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

    /**
     * Starts processing the dynamic Tiles definition by creating a temporary
     * definition을 얻는 작업을 수행한다. 사실상 가장 중요한 핵심 로직이다.
     * definition for rendering.
     */
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
            attr.setName(tilesBodyAttributeName);
            attr.setValue(url);

            AttributeContext attributeContext = container.startContext(request, response);
            attributeContext.putAttribute(tilesBodyAttributeName, attr);

            logger.debug("URL used for Tiles body.  url='" + url + "'.");
        }

        return definitionName;
    }

    /**
     * Closes the temporary Tiles definition.
     */
    protected void endDynamicDefinition(String definitionName, String beanName, HttpServletRequest request, HttpServletResponse response,
            TilesContainer container) {
        if (!definitionName.equals(beanName)) {
            container.endContext(request, response);
        }
    }

    /**
     * Processes values to get tiles template definition name. First a Tiles
     * definition matching the url is checked, then a url specific template is
     * checked, and then just the default root definition is used.
     * 졸 중요한 메소드로서  tiles에 정의한 세팅 파일을 읽어온다. 
     * 왜냐면 contriller에 이 정의는 2번 불가능하다. 따라서 템플릿이 1개 이상이면 이 메소드를 수정해야 한다.
     * 1. 캐싱된 derivedDefinitionName이 있는지 확인
     * 2. 하드코딩된 derivedDefinitionName이 있는지 확인
     * 3. root인지 확인 후 동적으로 derivedDefinitionName을 생성한다.
     * ex) URL이 test/html/keyCode 라면  => /test/html/root 으로 생성해준다.
     *     즉 현재는 트리형별로 템플릿을 변경 가능하게 되어있다.
     *     만약 popUp등의 용도로 템플릿을 더 만들 예정이라면 이 공식을 사용하거나 수정하자.
     * 4. 생성된 derivedDefinitionName이 설정파일에 하드코딩 되어있으면 사용한다. 
     * 5. 아니라면 메인 템플릿을 사용한다.
     * @return tiles2 설정파일(XML)에 반드시 기록되어있는 Definition 이름.
     * @throws TilesException
     *             If no valid Tiles definition is found.
     */
    protected String processTilesDefinitionName(String beanName, TilesContainer container, HttpServletRequest request,
            HttpServletResponse response) throws TilesException {
        // if definition already derived use it, otherwise 
        // check if url (bean name) is a template definition, then 
        // check for main template
        
        if (derivedDefinitionName != null) {  //캐싱되어 있다면
            return derivedDefinitionName;
        } else if (container.isValidDefinition(beanName, request, response)) {  //definition이 하드코딩된게 있다면 그걸 먼저 사용한다.
            derivedDefinitionName = beanName;
            return beanName;
        }
        
        //하드코딩 된게 없어서 다이나믹하게 생성해야 하는것들은 이제 시작~
        //여기서 result를 만드는 부분을 수정해서 사용하자!!!!.
        String result = null;

        StringBuilder sb = new StringBuilder();
        int lastIndex = beanName.lastIndexOf("/");
        boolean rootDefinition = false;

        // if delim, tiles def will start with it
        // 즉 tilesDefinitionDelimiter가 있다면 무조건 tilesDefinitionDelimiter로 시작해야 한다.
        if (StringUtils.hasLength(tilesDefinitionDelimiter)) {
            sb.append(tilesDefinitionDelimiter);
        }

        // if no '/', then at context root
        if (lastIndex == -1) {
            rootDefinition = true;
        } else {
            String path = beanName.substring(0, lastIndex);
            
            if (StringUtils.hasLength(tilesDefinitionDelimiter)) {
                path = StringUtils.replace(path, "/", tilesDefinitionDelimiter);
            }

            sb.append(path);

            if (StringUtils.hasLength(tilesDefinitionDelimiter)) {
                sb.append(tilesDefinitionDelimiter);
            }
        }

        sb.append(tilesDefinitionName); //test/html/keyCode.  => /test/html/root

        
        //새로 생성한 sb가 tiles설정파일에 하드코딩 되어있는지 검증한다.
        //문자열 생성공식을 바꾸고 리턴값을 바꾸도록 하다.
        if (container.isValidDefinition(sb.toString(), request, response)) {
            result = sb.toString();
        } else if (!rootDefinition) {
            String root = null;

            if (StringUtils.hasLength(tilesDefinitionDelimiter)) {
                root = tilesDefinitionDelimiter;
            }
            
            root += tilesDefinitionName;
            
            //영감추가. 최종 URL의 마지막 글자가 _이면 root의 끝 문자에 _를 더한걸로 정의한다.
            String name = Strings.getLast(beanName,tilesDefinitionDelimiter);
            for(String filter : FILTER){
                if(name.endsWith(filter)){
                    root += filter;
                    break;
                }
            }

            if (container.isValidDefinition(root, request, response)) {
                result = root;
            } else {
                throw new TilesException("No defintion of found for " + "'" + root + "'" + " or '" + sb.toString() + "'");
            }
        }

        derivedDefinitionName = result; //캐싱한다~

        return result;
    }

}
