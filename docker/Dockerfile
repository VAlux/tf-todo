FROM oracle/graalvm-ce:20.1.0

MAINTAINER Alexander Voievodin (alvo@alvo.dev)

RUN gu install native-image

WORKDIR /app

ADD target/scala-2.13/todo-assembly-0.0.1-SNAPSHOT.jar todo.jar

# built graal native image
# --static - creates a statically linked executable that has no dependencies on external libraries
# --verbose - verbose build output which useful for debugging
# --allows-incomplete-classpath - allows image building with an incomplete class path and reports type resolution errors at run time when they are accessed the first time, instead of during image building
# --report-unsupported-elements-at-runtime - reports usage of unsupported methods and fields at run time when they are accessed the first time, instead of as an error during image building
# --no-fallback - fail the build instead of generating fallback code when native image cannot resolve uses of reflection or other issues
RUN native-image --static --verbose --allow-incomplete-classpath --enable-https --enable-http --enable-all-security-services --initialize-at-build-time=scala,scala.runtime.Statics$VM --report-unsupported-elements-at-runtime  --no-fallback -jar todo.jar todo

# command
ENTRYPOINT ["/app/todo"]