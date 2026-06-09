<template>
  <v-row align="start">
    <v-col cols="12" md="5">
      <v-card elevation="2">
        <v-card-title>Invitar Usuario</v-card-title>
        <v-card-text>
          <v-alert v-if="inviteError" type="error" variant="tonal" class="mb-4" closable @click:close="inviteError = ''">
            {{ inviteError }}
          </v-alert>

          <v-alert v-if="inviteSuccess" type="success" variant="tonal" class="mb-4" closable @click:close="inviteSuccess = ''">
            Usuario invitado exitosamente. Contraseña temporal: <strong>{{ tempPassword }}</strong>
          </v-alert>

          <v-form @submit.prevent="handleInvite">
            <v-text-field
              v-model="form.email"
              label="Email"
              type="email"
              prepend-inner-icon="mdi-email"
              :rules="[rules.required, rules.email]"
              required
            />

            <v-select
              v-model="form.role"
              :items="roles"
              item-title="label"
              item-value="value"
              label="Rol"
              prepend-inner-icon="mdi-shield-account"
              :rules="[rules.required]"
              required
            />

            <v-select
              v-model="form.assignedWarehouseId"
              :items="warehouses"
              item-title="name"
              item-value="id"
              label="Bodega Asignada (Opcional)"
              prepend-inner-icon="mdi-warehouse"
              clearable
            />

            <v-btn
              type="submit"
              color="primary"
              block
              :loading="inviting"
            >
              Invitar
            </v-btn>
          </v-form>
        </v-card-text>
      </v-card>
    </v-col>

    <v-col cols="12" md="7">
      <v-card elevation="2">
        <v-card-title>Todos los Usuarios</v-card-title>
        <v-card-text>
          <v-progress-linear v-if="loading" indeterminate color="primary" />

          <v-table density="comfortable">
            <thead>
              <tr>
                <th>Email</th>
                <th>Nombre</th>
                <th>Rol</th>
                <th>Bodega</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="u in users" :key="u.id">
                <td>{{ u.email }}</td>
                <td>{{ u.nombre }}</td>
                <td>
                  <v-chip :color="chipColor(u.role)" size="small" variant="tonal">
                    {{ u.role }}
                  </v-chip>
                </td>
                <td>{{ u.assignedWarehouse?.name || 'Ninguna' }}</td>
                <td>
                  <v-chip :color="u.enabled ? 'success' : 'error'" size="small" variant="tonal">
                    {{ u.enabled ? 'Activo' : 'Inactivo' }}
                  </v-chip>
                </td>
                <td>
                  <v-btn icon="mdi-pencil" variant="text" size="small" color="primary" @click="openEditDialog(u)"></v-btn>
                  <v-btn 
                    :icon="u.enabled ? 'mdi-account-off' : 'mdi-account-check'" 
                    variant="text" 
                    size="small" 
                    :color="u.enabled ? 'error' : 'success'" 
                    @click="toggleActive(u)">
                  </v-btn>
                </td>
              </tr>
              <tr v-if="!users.length && !loading">
                <td colspan="6" class="text-center text-medium-emphasis py-6">
                  No hay usuarios registrados
                </td>
              </tr>
            </tbody>
          </v-table>
        </v-card-text>
      </v-card>

      <v-dialog v-model="editDialog" max-width="500">
        <v-card>
          <v-card-title>Editar Usuario</v-card-title>
          <v-card-text>
            <v-form @submit.prevent="handleEdit" ref="editForm">
              <v-select
                v-model="editingUser.role"
                :items="roles"
                item-title="label"
                item-value="value"
                label="Rol"
                required
              />
              <v-select
                v-model="editingUser.assignedWarehouseId"
                :items="warehouses"
                item-title="name"
                item-value="id"
                label="Bodega Asignada"
                clearable
              />
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn variant="text" @click="editDialog = false">Cancelar</v-btn>
            <v-btn color="primary" @click="handleEdit" :loading="saving">Guardar</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-col>
  </v-row>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUsersStore } from '../../stores/users'
import { getWarehouses } from '../../api/warehouses'

const store = useUsersStore()
const users = ref([])
const warehouses = ref([])
const loading = ref(false)
const inviting = ref(false)
const saving = ref(false)
const editDialog = ref(false)
const inviteError = ref('')
const inviteSuccess = ref(false)
const tempPassword = ref('')

const form = ref({
  email: '',
  role: null,
  assignedWarehouseId: null
})

const editingUser = ref({
  id: null,
  role: null,
  assignedWarehouseId: null
})

const roles = [
  { label: 'Admin', value: 'ADMIN' },
  { label: 'Local', value: 'LOCAL' },
  { label: 'Bodega', value: 'BODEGA' },
]

const rules = {
  required: (v) => !!v || 'Campo requerido',
  email: (v) => /.+@.+/.test(v) || 'Email inválido',
}

function chipColor(role) {
  const map = { ADMIN: 'warning', LOCAL: 'primary', BODEGA: 'success' }
  return map[role] || 'grey'
}

async function handleInvite() {
  inviting.value = true
  inviteError.value = ''
  inviteSuccess.value = false
  try {
    const result = await store.invite(form.value.email, form.value.role, form.value.assignedWarehouseId)
    tempPassword.value = result.temporaryPassword
    inviteSuccess.value = true
    form.value = { email: '', role: null, assignedWarehouseId: null }
    users.value = [...store.users]
  } catch (e) {
    inviteError.value = e.response?.data?.message || 'Error al invitar usuario'
  } finally {
    inviting.value = false
  }
}

function openEditDialog(u) {
  editingUser.value = {
    id: u.id,
    role: u.role,
    assignedWarehouseId: u.assignedWarehouse?.id || null
  }
  editDialog.value = true
}

async function handleEdit() {
  saving.value = true
  try {
    // Si no hay bodega, mandamos -1 para que el backend la limpie (como se definió en el servicio)
    const wid = editingUser.value.assignedWarehouseId || -1
    await store.update(editingUser.value.id, {
      role: editingUser.value.role,
      assignedWarehouseId: wid
    })
    users.value = [...store.users]
    editDialog.value = false
  } catch (e) {
    alert('Error al actualizar usuario')
  } finally {
    saving.value = false
  }
}

async function toggleActive(u) {
  try {
    await store.update(u.id, { active: !u.enabled })
    users.value = [...store.users]
  } catch (e) {
    alert('Error al cambiar estado')
  }
}

onMounted(async () => {
  loading.value = true
  try {
    const [wRes] = await Promise.all([getWarehouses(), store.fetchAll()])
    warehouses.value = wRes.data
    users.value = [...store.users]
  } finally {
    loading.value = false
  }
})
</script>
