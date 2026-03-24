# app.py
from flask import Flask, request, jsonify
from flask_cors import CORS
import json
import os
from datetime import datetime
from dotenv import load_dotenv
import hashlib

# Cargar variables del archivo .env
load_dotenv()

app = Flask(__name__)
CORS(app)  # Permite conexiones desde tu app Android

# ============================================
# CONFIGURACIÓN
# ============================================

NETWORK = os.getenv('NETWORK', 'devnet')
RPC_URL = os.getenv('RPC_URL', 'https://api.devnet.solana.com')
BACKEND_PORT = int(os.getenv('BACKEND_PORT', 5000))
OWNER_PUBLIC_KEY = os.getenv('OWNER_PUBLIC_KEY', 'Tu_Public_Key')
MINT_ADDRESS = os.getenv('MINT_ADDRESS', 'Tu_Mint_Address')

# ============================================
# BASE DE DATOS SIMPLE (en archivo JSON)
# ============================================

DB_FILE = 'rewards_db.json'

def load_database():
    """Cargar recompensas desde archivo"""
    if os.path.exists(DB_FILE):
        with open(DB_FILE, 'r') as f:
            return json.load(f)
    return []

def save_database(data):
    """Guardar recompensas en archivo"""
    with open(DB_FILE, 'w') as f:
        json.dump(data, f, indent=2)

# ============================================
# RUTAS (ENDPOINTS)
# ============================================

@app.route('/', methods=['GET'])
def health_check():
    """Verificar que el servidor está corriendo"""
    return jsonify({
        'status': 'ok',
        'message': 'Backend AllWays Solana está funcionando',
        'network': NETWORK,
        'timestamp': datetime.now().isoformat()
    }), 200


@app.route('/api/rewards/send', methods=['POST'])
def send_reward():
    """
    Recibe solicitud de recompensa desde el app Android
    
    Body esperado (JSON):
    {
        "recipientPublicKey": "Solana_Public_Key",
        "amount": 10,
        "reason": "accessibility_info",
        "metadata": {"placeId": "123"}
    }
    """
    try:
        # Obtener datos del request
        data = request.json
        
        if not data:
            return jsonify({
                'success': False,
                'message': 'Body vacío'
            }), 400
        
        # Validar campos requeridos
        recipient_public_key = data.get('recipientPublicKey')
        amount = data.get('amount')
        reason = data.get('reason')
        metadata = data.get('metadata', {})
        
        if not recipient_public_key or not amount or not reason:
            return jsonify({
                'success': False,
                'message': 'Faltan campos: recipientPublicKey, amount, reason'
            }), 400
        
        # Log en consola
        print(f"\n{'='*60}")
        print(f"📝 NUEVA RECOMPENSA SOLICITADA")
        print(f"{'='*60}")
        print(f"   Receptor: {recipient_public_key}")
        print(f"   Monto: {amount} tokens")
        print(f"   Razón: {reason}")
        print(f"   Timestamp: {datetime.now().isoformat()}")
        
        # ============================================
        # GENERAR HASH DE TRANSACCIÓN
        # (Más adelante será una transacción real)
        # ============================================
        
        transaction_hash = hashlib.sha256(
            f"{recipient_public_key}{amount}{reason}{datetime.now().isoformat()}".encode()
        ).hexdigest()[:44]
        
        # Crear registro
        reward_record = {
            'id': len(load_database()) + 1,
            'recipient': recipient_public_key,
            'amount': amount,
            'reason': reason,
            'transaction_hash': transaction_hash,
            'status': 'confirmed',
            'timestamp': datetime.now().isoformat(),
            'metadata': metadata
        }
        
        # Guardar en base de datos
        db = load_database()
        db.append(reward_record)
        save_database(db)
        
        print(f"   ✅ Transacción: {transaction_hash}")
        print(f"{'='*60}\n")
        
        # Responder al app Android
        return jsonify({
            'success': True,
            'transactionHash': transaction_hash,
            'message': f'Recompensa de {amount} tokens enviada por {reason}',
            'amount': amount,
            'reason': reason
        }), 200
        
    except Exception as e:
        print(f"\n❌ ERROR: {str(e)}\n")
        return jsonify({
            'success': False,
            'message': f'Error al procesar recompensa: {str(e)}'
        }), 500


@app.route('/api/rewards/history/<user_id>', methods=['GET'])
def get_reward_history(user_id):
    """
    Obtener historial de recompensas del usuario
    
    URL: /api/rewards/history/user123
    """
    try:
        db = load_database()
        
        # Filtrar recompensas del usuario
        user_rewards = [
            r for r in db 
            if r['recipient'] == user_id
        ]
        
        print(f"📊 Historial solicitado para: {user_id}")
        print(f"   Recompensas encontradas: {len(user_rewards)}")
        
        return jsonify({
            'rewards': user_rewards
        }), 200
        
    except Exception as e:
        return jsonify({
            'success': False,
            'message': f'Error al obtener historial: {str(e)}'
        }), 500


@app.route('/api/wallet/<user_id>', methods=['GET'])
def get_user_wallet(user_id):
    """
    Obtener información de wallet del usuario
    
    URL: /api/wallet/user123
    """
    try:
        db = load_database()
        
        # Contar tokens totales ganados
        total_tokens = sum(
            r['amount'] for r in db 
            if r['recipient'] == user_id
        )
        
        return jsonify({
            'userId': user_id,
            'publicKey': user_id,
            'balance': total_tokens
        }), 200
        
    except Exception as e:
        return jsonify({
            'success': False,
            'message': f'Error al obtener wallet: {str(e)}'
        }), 500


@app.route('/api/voting/cast', methods=['POST'])
def cast_vote():
    """
    Registrar un voto en una propuesta
    
    Body esperado:
    {
        "userId": "user123",
        "proposalId": "prop456",
        "voteOption": "yes",
        "tokensToSpend": 5
    }
    """
    try:
        data = request.json
        
        user_id = data.get('userId')
        proposal_id = data.get('proposalId')
        vote_option = data.get('voteOption')
        tokens_spent = data.get('tokensToSpend', 5)
        
        # Verificar que el usuario tenga suficientes tokens
        db = load_database()
        total_tokens = sum(
            r['amount'] for r in db 
            if r['recipient'] == user_id
        )
        
        if total_tokens < tokens_spent:
            return jsonify({
                'success': False,
                'message': f'No tienes suficientes tokens. Tienes {total_tokens}, necesitas {tokens_spent}'
            }), 400
        
        # Generar hash
        transaction_hash = hashlib.sha256(
            f"{user_id}{proposal_id}{vote_option}".encode()
        ).hexdigest()[:44]
        
        print(f"\n✅ Voto registrado:")
        print(f"   Usuario: {user_id}")
        print(f"   Propuesta: {proposal_id}")
        print(f"   Opción: {vote_option}")
        print(f"   Transacción: {transaction_hash}\n")
        
        return jsonify({
            'success': True,
            'message': f'Voto registrado: {vote_option}',
            'transactionHash': transaction_hash
        }), 200
        
    except Exception as e:
        return jsonify({
            'success': False,
            'message': f'Error al votar: {str(e)}'
        }), 500


@app.route('/api/admin/rewards', methods=['GET'])
def get_all_rewards():
    """
    Ver TODAS las recompensas (solo para testing)
    URL: /api/admin/rewards
    """
    try:
        db = load_database()
        return jsonify({
            'total_rewards': len(db),
            'rewards': db
        }), 200
    except Exception as e:
        return jsonify({
            'success': False,
            'message': f'Error: {str(e)}'
        }), 500


# ============================================
# EJECUTAR SERVIDOR
# ============================================

if __name__ == '__main__':
    print("\n" + "="*60)
    print("🚀 Backend AllWays Solana iniciando...")
    print("="*60)
    print(f"📍 Servidor corriendo en: http://localhost:{BACKEND_PORT}")
    print(f"🌐 Red: {NETWORK}")
    print(f"📂 Datos guardados en: {os.path.abspath(DB_FILE)}")
    print("="*60)
    print("\n⚠️  Para detener el servidor: Presiona Ctrl + C\n")
    print("="*60 + "\n")
    
    app.run(host='0.0.0.0', port=BACKEND_PORT, debug=True)
