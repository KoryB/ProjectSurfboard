package framework;

import framework.collisions.CollisionHandler;
import framework.math3d.primitives.AABB;
import framework.math3d.primitives.IntersectionHandler;
import framework.math3d.vec3;
import framework.math3d.mat4;

import java.util.Set;
import java.util.TreeSet;

import static JGL.JGL.*;
import static JSDL.JSDL.*;
import static framework.math3d.math3d.*;

import framework.math3d.vec2;
import framework.math3d.vec4;

public class Main2
{
    static InputHandler INPUT = new InputHandler("config/cfg.properties");

    public static void main(String[] args)
    {

        SDL_Init(SDL_INIT_VIDEO);
        long win = SDL_CreateWindow("ETGG 2802", 40, 60, 512, 512, SDL_WINDOW_OPENGL);
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_PROFILE_MASK, SDL_GL_CONTEXT_PROFILE_CORE);
        SDL_GL_SetAttribute(SDL_GL_DEPTH_SIZE, 24);
        SDL_GL_SetAttribute(SDL_GL_STENCIL_SIZE, 8);
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_MAJOR_VERSION, 3);
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_MINOR_VERSION, 2);
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_FLAGS, SDL_GL_CONTEXT_DEBUG_FLAG);
        SDL_GL_CreateContext(win);

        glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, 0, null, true);
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        glDebugMessageCallback(
                new DebugMessageCallback()
                {
                    @Override
                    public void debugCallback(int source, int type, int id, int severity, String message, Object obj)
                    {
                        System.out.println("GL message: " + message);
                        //if( severity == GL_DEBUG_SEVERITY_HIGH )
                        //    System.exit(1);
                    }
                },
                null);

        int[] tmp = new int[1];
        glGenVertexArrays(1, tmp);
        int vao = tmp[0];
        glBindVertexArray(vao);

        glClearColor(0.2f, 0.4f, 0.6f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        Set<Integer> keys = new TreeSet<>();
        Camera cam;
        Program prog;
        Program blurprog;
        float prev;
        UnitSquare usq;
        Framebuffer fbo1;
        Framebuffer fbo2;
        Texture2D dummytex = new SolidTexture(GL_UNSIGNED_BYTE, 0, 0, 0, 0);
        Player player = new Player(new vec4(-2.0, 1, -.5, 1));
        usq = new UnitSquare();

        Wall wall = new Wall(new vec4(0, 0, 0, 1));
        Wall wall2 = new Wall(new vec4(0.5, 0, 0, 1));
        Wall wall3 = new Wall(new vec4(0.5, 0, -1, 1));


        fbo1 = new Framebuffer(512, 512);
        fbo2 = new Framebuffer(512, 512);

        prog = new Program("vs.txt", "fs.txt");
        blurprog = new Program("blurvs.txt", "blurfs.txt");


        cam = new Camera();
        cam.lookAt(new vec3(-4.5, 3, -.5), new vec3(-3.5, 0, -1.5), normalize(new vec3(1, 0, -1)));

        prev = (float) (System.nanoTime() * 1E-9);
        while (true)
        {

            INPUT.poll();

            float now = (float) (System.nanoTime() * 1E-9);
            float elapsed = now - prev;
            //            elapsed *= 3;

            prev = now;

            if (INPUT.keyPressed(SDLK_w))
                cam.walk(0.5f * elapsed);
            if (INPUT.keyPressed(SDLK_s))
                cam.walk(-0.5f * elapsed);
            if (INPUT.keyPressed(SDLK_a))
                cam.strafe(new vec3(-0.4f * elapsed, 0, 0));
            if (INPUT.keyPressed(SDLK_d))
                cam.strafe(new vec3(0.4f * elapsed, 0, 0));
            if (INPUT.keyPressed(SDLK_q))
                cam.strafe(new vec3(0, -0.4f * elapsed, 0));
            if (INPUT.keyPressed(SDLK_e))
                cam.strafe(new vec3(0, 0.4f * elapsed, 0));
            if (INPUT.keyPressed(SDLK_r))
                cam.tilt(0.4f * elapsed);
            if (INPUT.keyPressed(SDLK_t))
                cam.tilt(-0.4f * elapsed);
            if (INPUT.keyPressed(SDLK_f))
                cam.pitch(0.4f * elapsed);
            if (INPUT.keyPressed(SDLK_g))
                cam.pitch(-0.4f * elapsed);

            if (INPUT.keyPressed(SDLK_SPACE))
                cam.getCollisionPrimitive().printInfo();

            //the fbo stuff is for later...
            //fbo1.bind();

            wall.update(elapsed);
            player.update(elapsed);
            //            CollisionHandler.pushApartAABB(cam, wall);
            /*if (IntersectionHandler.AABBAABBIntersection((AABB) cam.getCollisionPrimitive(), (AABB) wall
            .getCollisionPrimitive()))
            {
                System.out.println("Colliding!");
                cam.getCollisionPrimitive().printInfo();
                wall.getCollisionPrimitive().printInfo();

                CollisionHandler.pushApartAABB(wall, cam);
            }*/

            CollisionHandler.pushApartAABB(player, wall);
            CollisionHandler.pushApartAABB(player, wall2);
            CollisionHandler.pushApartAABB(player, wall3);


            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            prog.use();
            prog.setUniform("lightPos", new vec3(50, 50, 50));
            cam.draw(prog);
            wall.draw(prog);
            wall2.draw(prog);
            wall3.draw(prog);
            player.draw(prog);

            //fbo1.unbind();

            //this is also for later...
/*
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            blurprog.use();
            blurprog.setUniform("diffuse_texture",fbo1.texture);
            usq.draw(blurprog);
            blurprog.setUniform("diffuse_texture",dummytex);
*/

            SDL_GL_SwapWindow(win);


        }//endwhile
    }//end main
}
