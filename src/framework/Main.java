package framework;

import framework.math3d.vec3;
import framework.math3d.mat4;
import java.util.Set;
import java.util.TreeSet;
import static JGL.JGL.*;
import static JSDL.JSDL.*;

public class Main{
    
    
    public static void main(String[] args){
        
        SDL_Init(SDL_INIT_VIDEO);
        long win = SDL_CreateWindow("ETGG 2802",40,60, 512,512, SDL_WINDOW_OPENGL );
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_PROFILE_MASK,SDL_GL_CONTEXT_PROFILE_CORE);
        SDL_GL_SetAttribute(SDL_GL_DEPTH_SIZE,24);
        SDL_GL_SetAttribute(SDL_GL_STENCIL_SIZE,8);
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_MAJOR_VERSION,3);
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_MINOR_VERSION,2);
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_FLAGS, SDL_GL_CONTEXT_DEBUG_FLAG);
        SDL_GL_CreateContext(win);
        
        glDebugMessageControl(GL_DONT_CARE,GL_DONT_CARE,GL_DONT_CARE, 0,null, true );
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        glDebugMessageCallback(
                new DebugMessageCallback()
                {
                    @Override
                    public void debugCallback(int source, int type, int id, int severity, String message, Object obj)
                    {
                        System.out.println("GL message: " + message);
                        if (severity == GL_DEBUG_SEVERITY_HIGH)
                            System.exit(1);
                    }
                },
                null);

        int[] tmp = new int[1];
        glGenVertexArrays(1,tmp);
        int vao = tmp[0];
        glBindVertexArray(vao);

        glClearColor(0.2f,0.4f,0.6f,1.0f);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        Set<Integer> keys = new TreeSet<>();
        Camera cam;
        Program prog;
        Program blurprog;
        float prev;
        Mesh column;
        UnitSquare usq;
        
        usq = new UnitSquare();
        ImageTextureArray ita = new ImageTextureArray("assets/globe%02d.png",24);
        prog = new Program("vs.txt","fs.txt");
        cam = new Camera();
        cam.lookAt( new vec3(0,0,5), new vec3(0,0,0), new vec3(0,1,0) );

        prev = (float)(System.nanoTime()*1E-9);

        float framenum = 0.0f;
        SDL_Event ev=new SDL_Event();
        while(true){
            while(true){
                int rv = SDL_PollEvent(ev);
                if( rv == 0 )
                    break;
                if( ev.type == SDL_QUIT )
                    System.exit(0);
                if( ev.type == SDL_MOUSEMOTION ){
                }
                if( ev.type == SDL_KEYDOWN ){
                    keys.add(ev.key.keysym.sym);
                }
                if( ev.type == SDL_KEYUP ){
                    keys.remove(ev.key.keysym.sym);
                }
            }

            float now = (float)(System.nanoTime()*1E-9);
            float elapsed = now-prev;

            framenum += 16.0f*elapsed;
            while( framenum >= 24.0f )
                framenum -= 24.0f;
            
            prev=now;

            if( keys.contains(SDLK_w ))
                cam.walk(0.5f*elapsed);
            if( keys.contains(SDLK_s))
                cam.walk(-0.5f*elapsed);
            if( keys.contains(SDLK_a))
                cam.turn(0.4f*elapsed);
            if( keys.contains(SDLK_d))
                cam.turn(-0.4f*elapsed);
            if( keys.contains(SDLK_r))
                cam.tilt(0.4f*elapsed);
            if( keys.contains(SDLK_t))
                cam.tilt(-0.4f*elapsed);


            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            prog.use();
            prog.setUniform("diffuse_texture",ita);
            prog.setUniform("framenumber",framenum);
            prog.setUniform("lightPos",new vec3(50,50,50) );
            cam.draw(prog);
            prog.setUniform("worldMatrix",mat4.identity());
            usq.draw(prog);
            
            
            SDL_GL_SwapWindow(win);
        }//endwhile
    }//end main
}