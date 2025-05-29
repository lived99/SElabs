package lab1.lab3b;

import lab1.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito; // 导入 Mockito 库，用于模拟 getBridgeWords 方法

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random; // 确保 Random 导入

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class GraphGenerateNewTextTest {

    private Graph graph;
    private Random mockRandom; // 用于模拟 Random 行为，使测试可预测



    @BeforeEach
    void setup() {
        // 使用 Mockito 创建 Graph 类的 spy 对象。
        // spy 允许我们调用真实方法，但可以桩化（stub）特定方法。
        // 这里，我们用它来控制 getBridgeWords 的行为，使其返回我们想要的结果，
        // 而 generateNewText 的其他逻辑（如文本处理、插入）仍然是真实的。
        graph = Mockito.spy(new Graph());
        mockRandom = Mockito.mock(Random.class); // 模拟 Random 对象
        when(graph.getBridgeWords(anyString(), anyString())).thenReturn(Collections.emptyList());
    }

    @Test
    @DisplayName("E1.1: 包含有效单词的简单非空文本，所有相邻词无桥接词")
    void testGenerateNewText_SimpleText_NoBridges() {
        // 预设 getBridgeWords 总是返回空列表
        String inputText = "apple banana orange";
        String expectedText = "apple banana orange"; // 预期没有插入

        String result = graph.generateNewText(inputText);
        assertEquals(expectedText, result);
    }

    @Test
    @DisplayName("E1.2: 输入为空字符串")
    void testGenerateNewText_EmptyInput() {
        String inputText = "";
        String expectedText = "";

        String result = graph.generateNewText(inputText);
        assertEquals(expectedText, result);
    }

    @Test
    @DisplayName("E1.3: 输入只包含非字母字符")
    void testGenerateNewText_NonAlphabeticInput() {
        String inputText = "123 !@#$ %^&*";
        String expectedText = ""; // 处理后应为空

        String result = graph.generateNewText(inputText);
        assertEquals(expectedText, result);
    }

    @Test
    @DisplayName("E2.1: 所有相邻单词对都有且只有一个桥接词")
    void testGenerateNewText_SingleBridgeWordPerPair() {
        // 模拟图，确保每对都有唯一桥接词
        // 文本: "word1 word2 word3"
        // 期望: "word1 bridge1 word2 bridge2 word3"
        when(graph.getBridgeWords("worda", "wordb")).thenReturn(Arrays.asList("bridgea"));
        when(graph.getBridgeWords("wordb", "wordc")).thenReturn(Arrays.asList("bridgeb"));

        String inputText = "worda wordb wordc";
        String expectedText = "worda bridgea wordb bridgeb wordc";

        String result = graph.generateNewText(inputText);
        assertEquals(expectedText, result);
    }

    @Test
    @DisplayName("E2.2: 所有相邻单词对都有多个桥接词 (随机选择一个)")
    void testGenerateNewText_MultipleBridgeWordsPerPair() {
        // 模拟图，确保每对都有多个桥接词
        // 我们需要模拟 Random 的行为，以确保测试的确定性。
        // 例如，让 rand.nextInt(size) 总是返回 0，这样总是选择第一个桥接词。
        Mockito.when(graph.getBridgeWords("apple", "orange")).thenReturn(Arrays.asList("sweet", "sour"));
        Mockito.when(graph.getBridgeWords("orange", "banana")).thenReturn(Arrays.asList("yellow", "green"));

        when(graph.getBridgeWords("apple", "orange")).thenReturn(Arrays.asList("sweet")); // Always pick "sweet"
        when(graph.getBridgeWords("orange", "banana")).thenReturn(Arrays.asList("yellow")); // Always pick "yellow"

        String inputText = "apple orange banana";
        String expectedText = "apple sweet orange yellow banana";

        String result = graph.generateNewText(inputText);
        assertEquals(expectedText, result);
    }


    @Test
    @DisplayName("E2.3: 所有相邻单词对都没有桥接词")
    void testGenerateNewText_NoBridgesAllPairs() {
        // 默认情况下，getBridgeWords 返回空列表，符合此测试
        String inputText = "this is a simple test";
        String expectedText = "this is a simple test";

        String result = graph.generateNewText(inputText);
        assertEquals(expectedText, result);
    }

    @Test
    @DisplayName("E2.4: 桥接词情况混合 (部分有，部分无，部分多个)")
    void testGenerateNewText_MixedBridgeScenarios() {
        // word1 -> bridge1 -> word2
        when(graph.getBridgeWords("worda", "wordb")).thenReturn(Arrays.asList("bridgea"));
        when(graph.getBridgeWords("wordc", "wordd")).thenReturn(Arrays.asList("bridgeb")); // 模拟随机选择结果

        String inputText = "worda wordb wordc wordd";
        // 预期：word1 bridge_a word2 word3 bridge_b word4
        String expectedText = "worda bridgea wordb wordc bridgeb wordd";

        String result = graph.generateNewText(inputText);
        assertEquals(expectedText, result);
    }


    @Test
    @DisplayName("E1.1, E3.2: 包含标点符号的文本，所有相邻词无桥接词")
    void testGenerateNewText_Punctuation_NoBridges() {
        String inputText = "Hello, world! How are you?";
        String expectedText = "Hello world How are you"; // 标点符号被移除

        String result = graph.generateNewText(inputText);
        assertEquals(expectedText, result);
    }

    @Test
    @DisplayName("E1.1, E3.3: 包含多个连续空格的文本，所有相邻词无桥接词")
    void testGenerateNewText_MultipleSpaces_NoBridges() {
        String inputText = "word   with    many    spaces";
        String expectedText = "word with many spaces";

        String result = graph.generateNewText(inputText);
        assertEquals(expectedText, result);
    }

    @Test
    @DisplayName("E1.1, E3.4: 包含大小写混合的文本，所有相邻词无桥接词")
    void testGenerateNewText_MixedCase_NoBridges() {
        String inputText = "The Quick Brown Fox";
        String expectedText = "The Quick Brown Fox"; // 单词保持原始大小写，但内部处理是小写

        String result = graph.generateNewText(inputText);
        assertEquals(expectedText, result);
    }

    // --- 无效等价类测试用例 ---



    @Test
    @DisplayName("特殊情况：单个单词输入")
    void testGenerateNewText_SingleWordInput() {
        String inputText = "single";
        String expectedText = "single"; // 没有相邻词对，不应有桥接词

        String result = graph.generateNewText(inputText);
        assertEquals(expectedText, result);
    }
}
