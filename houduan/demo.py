from flask import Flask, request, jsonify
import torch
from transformers import AutoTokenizer, AutoModelForCausalLM

app = Flask(__name__)

# 加载模型和tokenizer
model_path = "E:/Qwen/Qwen2.5-1.5B-Instruct"
tokenizer = AutoTokenizer.from_pretrained(model_path, trust_remote_code=True)
model = AutoModelForCausalLM.from_pretrained(model_path, trust_remote_code=True)

# 存储对话历史（使用结构化格式）
conversation_history = []

@app.route("/chat", methods=["POST"])
def chat():
    global conversation_history

    # 获取用户输入
    data = request.get_json()
    user_message = data.get("message", "")

    # 将用户消息加入对话历史
    conversation_history.append({"role": "user", "content": user_message})

    try:
        # 使用apply_chat_template构建正确格式
        input_ids = tokenizer.apply_chat_template(
            conversation_history,
            tokenize=True,
            add_generation_prompt=True,
            return_tensors="pt"
        ).to(model.device)

        # 生成参数设置
        outputs = model.generate(
            input_ids,
            max_new_tokens=512,  # 控制生成的新token数量
            do_sample=True,
            temperature=0.7,
            top_p=0.9,
            eos_token_id=tokenizer.eos_token_id,  # 设置结束符
            pad_token_id=tokenizer.pad_token_id   # 设置填充符
        )

        # 解码时跳过特殊token和输入部分
        full_response = tokenizer.decode(
            outputs[0][input_ids.shape[-1]:],  # 只取新生成的部分
            skip_special_tokens=True
        ).strip()

        # 处理可能的截断（查找最后一个标点作为自然结束点）
        last_punctuation = max(
            full_response.rfind("."),
            full_response.rfind("?"),
            full_response.rfind("!")
        )
        if last_punctuation != -1:
            clean_response = full_response[:last_punctuation+1]
        else:
            clean_response = full_response

        # 将AI回复加入对话历史
        conversation_history.append({"role": "assistant", "content": clean_response})

        return jsonify({"response": clean_response})

    except Exception as e:
        # 处理过长上下文：删除最早的两轮对话
        if "exceeds maximum" in str(e):
            conversation_history = conversation_history[2:]
            return jsonify({"response": "请再问一次"})
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)