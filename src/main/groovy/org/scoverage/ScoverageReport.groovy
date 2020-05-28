package org.scoverage

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.TaskAction
import scoverage.report.CoverageAggregator

import static org.gradle.api.tasks.PathSensitivity.RELATIVE

@CacheableTask
class ScoverageReport extends DefaultTask {

    @Nested
    ScoverageRunner runner

    @InputDirectory
    @PathSensitive(RELATIVE)
    final Property<File> dataDir = project.objects.property(File)

    @InputDirectory
    @PathSensitive(RELATIVE)
    final Property<File> sources = project.objects.property(File)

    @OutputDirectory
    final Property<File> reportDir = project.objects.property(File)

    @Input
    final Property<String> sourceEncoding = project.objects.property(String)

    @Input
    final Property<Boolean> coverageOutputCobertura = project.objects.property(Boolean)
    @Input
    final Property<Boolean> coverageOutputXML = project.objects.property(Boolean)
    @Input
    final Property<Boolean> coverageOutputHTML = project.objects.property(Boolean)
    @Input
    final Property<Boolean> coverageDebug = project.objects.property(Boolean)

    @TaskAction
    def report() {
        runner.run {
            reportDir.get().delete()
            reportDir.get().mkdirs()

            def coverage = CoverageAggregator.aggregate([dataDir.get()] as File[])

            if (coverage.isEmpty()) {
                project.logger.info("[scoverage] Could not find coverage file, skipping...")
            } else {
                new ScoverageWriter(project.logger).write(
                        sources.get(),
                        reportDir.get(),
                        coverage.get(),
                        sourceEncoding.get(),
                        coverageOutputCobertura.get(),
                        coverageOutputXML.get(),
                        coverageOutputHTML.get(),
                        coverageDebug.get())
            }
        }
    }
}
