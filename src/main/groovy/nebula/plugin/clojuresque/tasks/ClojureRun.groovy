package nebula.plugin.clojuresque.tasks

import kotka.gradle.utils.ConfigureUtil
import kotka.gradle.utils.Delayed
import nebula.plugin.clojuresque.Util
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.Option;

class ClojureRun extends ClojureSourceTask {

    @InputFiles
    @Delayed
    def classpath

    @Delayed
    def jvmOptions

    private String fn;

    @Option(option = "fn", description = "The clojure function (and optional args) to execute.")
    public void setFn(String fn) {
        this.fn = fn;
    }

    // Example usage: ./gradlew clojureRun --fn='my-ns/my-fn arg1 arg2'
    @TaskAction
    void run() {

        def options = [
            fn: fn
        ]

        def runtime = [
            "clojuresque/util.clj",
            "clojuresque/tasks/run.clj"
        ].collect { owner.class.classLoader.getResourceAsStream it }

        project.clojureexec {
            ConfigureUtil.configure delegate, this.jvmOptions
            classpath = project.files(
                this.srcDirs,
                this.classpath
            )
            standardInput = Util.toInputStream([
                runtime,
                "(clojuresque.tasks.run/main)",
                Util.optionsToStream(options)
            ])
        }
    }
}
