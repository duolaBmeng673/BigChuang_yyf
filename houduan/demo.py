from flask import Flask, request, jsonify
import torch
from transformers import AutoTokenizer, AutoModelForCausalLM

# 初始化 Flask app
app = Flask(__name__)

# 加载Qwen模型
model_path = "E:/Qwen/Qwen2.5-1.5B-Instruct"  # 模型路径
tokenizer = AutoTokenizer.from_pretrained(model_path)
model = AutoModelForCausalLM.from_pretrained(model_path)

# 存储对话历史
conversation_history = []

# 定义生成响应的接口
@app.route("/chat", methods=["POST"])
def chat():
    global conversation_history  # 使用全局对话历史

    # 获取用户输入的消息
    data = request.get_json()
    user_message = data.get("message", "")

    # 更新对话历史：每次添加用户的消息
    conversation_history.append(f"User: {user_message}")

    # 将对话历史编码为模型输入
    conversation_input = "\n".join(conversation_history)
    inputs = tokenizer(conversation_input, return_tensors="pt", padding=True, truncation=True, max_length=1024)

    # 生成模型输出
    outputs = model.generate(inputs["input_ids"], max_length=150, num_return_sequences=1)

    # 解码模型输出
    response_message = tokenizer.decode(outputs[0], skip_special_tokens=True)

    # 获取机器人回复的部分
    bot_reply = response_message.split("Bot:")[-1].strip()

    # 更新对话历史：将模型的回复加入历史
    conversation_history.append(f"Bot: {bot_reply}")

    # 返回生成的消息
    return jsonify({"response": bot_reply})

if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)
