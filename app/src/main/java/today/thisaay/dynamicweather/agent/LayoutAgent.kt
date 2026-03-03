package today.thisaay.dynamicweather.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

/**
 * Stateless AI agent that reshapes the UI layout JSON based on a user request.
 *
 * Each call creates a fresh agent instance with the current layout JSON baked into
 * the user message — no conversation history is kept between calls.
 */
class LayoutAgent(private val openAiApiKey: String) {

    /**
     * Given the [currentLayoutJson] and a natural-language [userRequest],
     * returns a new JSON string representing the updated UI layout.
     *
     * The agent is instructed to return ONLY raw JSON — no markdown, no prose.
     */
    suspend fun requestLayoutChange(
        currentLayoutJson: String,
        userRequest: String,
    ): String {
        val agent = AIAgent(
            promptExecutor = simpleOpenAIExecutor(openAiApiKey),
            systemPrompt = UISchemaPrompt.SYSTEM,
            llmModel = OpenAIModels.Chat.GPT5,
        )

        val prompt = buildString {
            appendLine("Current layout JSON:")
            appendLine(currentLayoutJson)
            appendLine()
            appendLine("User request: $userRequest")
            appendLine()
            appendLine("Return ONLY the updated layout JSON. No markdown, no explanation.")
        }

        return agent.run(prompt) ?: currentLayoutJson
    }
}
